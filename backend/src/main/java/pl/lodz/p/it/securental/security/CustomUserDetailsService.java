package pl.lodz.p.it.securental.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.annotations.RequiresNewTransaction;
import pl.lodz.p.it.securental.entities.accounts.Account;
import pl.lodz.p.it.securental.entities.accounts.MaskedPassword;
import pl.lodz.p.it.securental.repositories.accounts.AccountRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static pl.lodz.p.it.securental.exceptions.accounts.AccountNotFoundException.KEY_ACCOUNT_NOT_FOUND;
import static pl.lodz.p.it.securental.utils.StringUtils.integerArrayToString;

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
                    if (integerArrayToString(account.getAuthenticationToken().getCombination().stream().mapToInt(i -> i).toArray()).equals(combination)) {
                        System.out.println("gaz");
                        //TODO authorities
                        return new CustomUserDetails(
                                username,
                                combination,
                                maskedPassword.getHash(),
                                account.isConfirmed(),
                                true,
                                true,
                                account.isActive(),
                                Collections.singletonList(new SimpleGrantedAuthority("USER")));
                }
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
            //TODO authorities
            return new CustomUserDetails(
                    username,
                    null,
                    "",
                    account.isConfirmed(),
                    true,
                    true,
                    account.isActive(),
                    Collections.singletonList(new SimpleGrantedAuthority("USER")));
        }
    }
}
