package pl.lodz.p.it.securental.security;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import pl.lodz.p.it.securental.adapters.mok.AccountAdapter;
import pl.lodz.p.it.securental.entities.mok.Account;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.mok.AuthenticationFailedException;
import pl.lodz.p.it.securental.utils.ApplicationProperties;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticationProviderImpl extends AbstractUserDetailsAuthenticationProvider {

    private final AccountAdapter accountAdapter;
    private final GoogleAuthenticator googleAuthenticator;
    private final PasswordEncoder passwordEncoder;
    private final PlatformTransactionManager transactionManager;
    private final UserDetailsService userDetailsService;

    @Override
    @SneakyThrows
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) {
        AuthenticationTokenImpl auth = (AuthenticationTokenImpl) authentication;
        String principal = auth.getPrincipal().toString();

        if (!passwordEncoder.matches(auth.getCredentials().toString(), userDetails.getPassword())) {
            throw new AuthenticationFailedException();
        }

        if (principal.equals(ApplicationProperties.ADMIN_PRINCIPAL)) {
            handleFirstAdminLogin(auth, principal);
        } else if (!googleAuthenticator.authorizeUser(principal, auth.getOtpCode())) {
            throw new AuthenticationFailedException();
        }
    }

    @Override
    @SneakyThrows
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) {
        AuthenticationTokenImpl auth = (AuthenticationTokenImpl) authentication;
        return userDetailsService.loadUserForAuthentication(auth.getPrincipal().toString());
    }

    private void handleFirstAdminLogin(AuthenticationTokenImpl auth, String principal) throws ApplicationBaseException {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        Optional<Account> accountOptional = transactionTemplate.execute(transactionStatus -> {
            try {
                return accountAdapter.getAccount(principal);
            } catch (ApplicationBaseException e) {
                return Optional.empty();
            }
        });
        if (!Objects.isNull(accountOptional) && accountOptional.isPresent()) {
            Account account = accountOptional.get();
            if (account.getLastSuccessfulAuthentication() == null) {
                if (!auth.getOtpCode().equals(ApplicationProperties.ADMIN_OTP_CODE)) {
                    throw new AuthenticationFailedException();
                }
            } else if (!googleAuthenticator.authorizeUser(principal, auth.getOtpCode())) {
                throw new AuthenticationFailedException();
            }
        }
    }
}
