package pl.lodz.p.it.securental.security;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import pl.lodz.p.it.securental.adapters.mok.AccountAdapter;
import pl.lodz.p.it.securental.aop.annotations.RequiresNewTransaction;
import pl.lodz.p.it.securental.entities.mok.Account;
import pl.lodz.p.it.securental.utils.ApplicationProperties;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@RequiresNewTransaction
public class AuthenticationProviderImpl extends AbstractUserDetailsAuthenticationProvider {

    private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;
    private final GoogleAuthenticator googleAuthenticator;
    private final AccountAdapter accountAdapter;

    private String userNotFoundEncodedPassword;

    @Override
    @SneakyThrows
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        AuthenticationTokenImpl auth = (AuthenticationTokenImpl) authentication;
        String principal = auth.getPrincipal().toString();

        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");
            throw new BadCredentialsException(
                    messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }

        /*
        //TODO
        dane inicjujace:
        admin, secret jakis tam
        pierwsze logowanie kodem na sztywno ADMIN_OTP_CODE
        przejscie do szczegolow konta, generate qr code
        kolejne logowania kodami z telefonu
         */
        if (principal.equals(ApplicationProperties.ADMIN_PRINCIPAL)) {
            Optional<Account> accountOptional = accountAdapter.getAccount(principal);
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                if (account.getLastSuccessfulAuthentication() == null
                        && !auth.getOtpCode().equals(ApplicationProperties.ADMIN_OTP_CODE)) {
                    throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
                }
            }
        }

        if (!googleAuthenticator.authorizeUser(principal, auth.getOtpCode())) {
            throw new BadCredentialsException(
                    messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }

        String presentedPassword = authentication.getCredentials()
                .toString();

        if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            logger.debug("Authentication failed: password does not match stored value");
            throw new BadCredentialsException(
                    messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
    }

    @Override
    protected void doAfterPropertiesSet() {
        Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
        this.userNotFoundEncodedPassword = this.passwordEncoder.encode(USER_NOT_FOUND_PASSWORD);
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        AuthenticationTokenImpl auth = (AuthenticationTokenImpl) authentication;
        UserDetails loadedUser;

        try {
            loadedUser = this.userDetailsService.loadUserByUsernameAndCombination(auth.getPrincipal()
                    .toString(), auth.getCombination());
        } catch (UsernameNotFoundException notFound) {
            if (authentication.getCredentials() != null) {
                String presentedPassword = authentication.getCredentials()
                        .toString();
                passwordEncoder.matches(presentedPassword, userNotFoundEncodedPassword);
            }
            throw notFound;
        }

        if (loadedUser == null) {
            throw new InternalAuthenticationServiceException("UserDetailsService returned null, "
                    + "which is an interface contract violation");
        }
        return loadedUser;
    }

}
