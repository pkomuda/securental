package pl.lodz.p.it.securental.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.annotations.RequiresNewTransaction;
import pl.lodz.p.it.securental.entities.mok.AccessLevel;
import pl.lodz.p.it.securental.entities.mok.Account;
import pl.lodz.p.it.securental.entities.mok.MaskedPassword;
import pl.lodz.p.it.securental.repositories.mok.AccountRepository;

import java.util.*;
import java.util.stream.Collectors;

import static pl.lodz.p.it.securental.exceptions.mok.AccountNotFoundException.KEY_ACCOUNT_NOT_FOUND;
import static pl.lodz.p.it.securental.utils.ApplicationProperties.*;
import static pl.lodz.p.it.securental.utils.StringUtils.integerArrayToString;

@Slf4j
@Service
@AllArgsConstructor
@RequiresNewTransaction
public class CustomUserDetailsService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDetails loadUserByUsernameAndCombination(String username, String combination) throws UsernameNotFoundException {
        Optional<Account> accountOptional = accountRepository.findByOtpCredentialsUsername(username);
        if (accountOptional.isEmpty()) {
             throw new UsernameNotFoundException(KEY_ACCOUNT_NOT_FOUND);
        } else {
            Account account = accountOptional.get();
            for (MaskedPassword maskedPassword : account.getCredentials().getMaskedPasswords()) {
                if (passwordEncoder.matches(combination, maskedPassword.getCombination())) {
                    List<Integer> combinationList = account.getAuthenticationToken().getCombination();
                    String combinationString = combination;
//                    if (integerArrayToString(account.getAuthenticationToken().getCombination().stream().mapToInt(i -> i).toArray()).equals(combination)) {
                        return new CustomUserDetails(
                                username,
                                combination,
                                maskedPassword.getHash(),
                                account.isConfirmed(),
                                true,
                                true,
                                account.isActive(),
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
            throw new UsernameNotFoundException(KEY_ACCOUNT_NOT_FOUND);
        } else {
            Account account = accountOptional.get();
            return new CustomUserDetails(
                    username,
                    null,
                    "",
                    account.isConfirmed(),
                    true,
                    true,
                    account.isActive(),
                    getUserBackendRoles(account));
        }
    }

    private String[] getRolesForGroup(String groupName) {
        switch (groupName) {
            case ACCESS_LEVEL_ADMIN:
                return ADMIN_ROLES;
            case ACCESS_LEVEL_CLIENT:
                return CLIENT_ROLES;
            case ACCESS_LEVEL_EMPLOYEE:
                return EMPLOYEE_ROLES;
            default:
                return new String[0];
        }
    }

    private Set<String> getUserGroupNames(Account account) {
        return account.getAccessLevels().stream()
                .filter(AccessLevel::isActive)
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
