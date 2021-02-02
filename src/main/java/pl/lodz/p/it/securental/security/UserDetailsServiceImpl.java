package pl.lodz.p.it.securental.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.aop.annotations.RequiresNewTransaction;
import pl.lodz.p.it.securental.entities.mok.AccessLevel;
import pl.lodz.p.it.securental.entities.mok.Account;
import pl.lodz.p.it.securental.entities.mok.MaskedPassword;
import pl.lodz.p.it.securental.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.securental.repositories.mok.AccountRepository;
import pl.lodz.p.it.securental.utils.ApplicationProperties;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@RequiresNewTransaction
public class UserDetailsServiceImpl {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDetails loadUserByUsernameAndCombination(String username, String combination) throws UsernameNotFoundException {
        Optional<Account> accountOptional = accountRepository.findByOtpCredentialsUsername(username);
        if (accountOptional.isEmpty()) {
             throw new UsernameNotFoundException(AccountNotFoundException.KEY_ACCOUNT_NOT_FOUND);
        } else {
            Account account = accountOptional.get();
            for (MaskedPassword maskedPassword : account.getCredentials().getMaskedPasswords()) {
                if (passwordEncoder.matches(combination, maskedPassword.getCombination())) {
                    List<Integer> combinationList = account.getAuthenticationToken().getCombination();
                    String combinationString = combination;
//                    if (integerArrayToString(account.getAuthenticationToken().getCombination().stream().mapToInt(i -> i).toArray()).equals(combination)) {
                        return new UserDetailsImpl(
                                username,
                                combination,
                                maskedPassword.getHash(),
                                account.getConfirmed(),
                                true,
                                true,
                                account.getActive(),
                                getUserFrontendRoles(account));
//                }
                }
            }
            return null;
        }
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> accountOptional = accountRepository.findByOtpCredentialsUsername(username);
        if (accountOptional.isEmpty()) {
            throw new UsernameNotFoundException(AccountNotFoundException.KEY_ACCOUNT_NOT_FOUND);
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

    private Set<String> getUserGroupNames(Account account) {
        return account.getAccessLevels().stream()
                .filter(AccessLevel::getActive)
                .map(AccessLevel::getName)
                .collect(Collectors.toSet());
    }

    private Set<SimpleGrantedAuthority> getUserFrontendRoles(Account account) {
        return getUserGroupNames(account).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
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

//    private Set<SimpleGrantedAuthority> getUserRoles(Account account) {
//        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
//        List<String> groups = account.getAccessLevels().stream()
//                .filter(AccessLevel::isActive)
//                .map(AccessLevel::getName)
//                .collect(Collectors.toList());
//        for (String group : groups) {
//            for (String role : getRolesForGroup(group)) {
//                authorities.add(new SimpleGrantedAuthority(role));
//            }
//        }
//        return authorities;
//    }
//
//    public static Set<SimpleGrantedAuthority> getUserRoles(List<String> groups) {
//        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
//        for (String group : groups) {
//            for (String role : getRolesForGroup(group)) {
//                authorities.add(new SimpleGrantedAuthority(role));
//            }
//        }
//        return authorities;
//    }
}
