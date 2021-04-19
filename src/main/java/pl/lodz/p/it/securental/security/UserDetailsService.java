package pl.lodz.p.it.securental.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.aop.annotations.RequiresNewTransaction;
import pl.lodz.p.it.securental.configuration.persistence.MokConfiguration;
import pl.lodz.p.it.securental.entities.mok.AccessLevel;
import pl.lodz.p.it.securental.entities.mok.Account;
import pl.lodz.p.it.securental.entities.mok.AuthenticationToken;
import pl.lodz.p.it.securental.entities.mok.MaskedPassword;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.securental.exceptions.mok.AuthenticationFailedException;
import pl.lodz.p.it.securental.repositories.mok.AccountRepository;
import pl.lodz.p.it.securental.utils.ApplicationProperties;
import pl.lodz.p.it.securental.utils.StringUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@RequiresNewTransaction(MokConfiguration.MOK_TRANSACTION_MANAGER)
public class UserDetailsService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDetails loadUserForAuthentication(String username) throws ApplicationBaseException {
        Optional<Account> accountOptional = accountRepository.findByOtpCredentialsUsername(username);
        if (accountOptional.isEmpty()) {
             throw new AccountNotFoundException();
        } else {
            Account account = accountOptional.get();
            AuthenticationToken authenticationToken = account.getAuthenticationToken();
            if (authenticationToken.getExpiration().isBefore(LocalDateTime.now())) {
                throw new AuthenticationFailedException();
            }
            String combination = StringUtils.integerCollectionToString(authenticationToken.getCombination());
            for (MaskedPassword maskedPassword : account.getCredentials().getMaskedPasswords()) {
                if (passwordEncoder.matches(combination, maskedPassword.getCombination())) {
                        return new UserDetailsImpl(
                                username,
                                combination,
                                maskedPassword.getHash(),
                                account.getConfirmed(),
                                true,
                                true,
                                account.getActive(),
                                getUserFrontendRoles(account));
                }
            }
            return null;
        }
    }

    private Set<SimpleGrantedAuthority> getUserFrontendRoles(Account account) {
        return getUserGroupNames(account).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    public UserDetails loadUserForAuthorization(String username) throws ApplicationBaseException {
        Optional<Account> accountOptional = accountRepository.findByOtpCredentialsUsername(username);
        if (accountOptional.isEmpty()) {
            throw new AccountNotFoundException();
        } else {
            Account account = accountOptional.get();
            return new UserDetailsImpl(
                    username,
                    null,
                    "",
                    account.getConfirmed(),
                    true,
                    true,
                    account.getActive(),
                    getUserBackendRoles(account));
        }
    }

    private Set<SimpleGrantedAuthority> getUserBackendRoles(Account account) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        for (String group : getUserGroupNames(account)) {
            for (String role : getRolesForGroup(group)) {
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }
        return authorities;
    }

    private Set<String> getUserGroupNames(Account account) {
        return account.getAccessLevels().stream()
                .filter(AccessLevel::getActive)
                .map(AccessLevel::getName)
                .collect(Collectors.toSet());
    }

    private String[] getRolesForGroup(String groupName) {
        switch (groupName) {
            case ApplicationProperties.ACCESS_LEVEL_ADMIN:
                return ApplicationProperties.ADMIN_ROLES;
            case ApplicationProperties.ACCESS_LEVEL_CLIENT:
                return ApplicationProperties.CLIENT_ROLES;
            case ApplicationProperties.ACCESS_LEVEL_EMPLOYEE:
                return ApplicationProperties.EMPLOYEE_ROLES;
            default:
                return new String[0];
        }
    }
}
