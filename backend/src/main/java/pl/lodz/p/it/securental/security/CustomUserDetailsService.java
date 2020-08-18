package pl.lodz.p.it.securental.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.it.securental.entities.accounts.Account;
import pl.lodz.p.it.securental.entities.accounts.Password;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.repositories.accounts.AccountRepository;

import java.util.Collections;
import java.util.Optional;

import static pl.lodz.p.it.securental.exceptions.accounts.AccountNotFoundException.KEY_ACCOUNT_NOT_FOUND;

@Service
@AllArgsConstructor
public class CustomUserDetailsService {

    private final AccountRepository accountRepository;

    @Transactional(rollbackFor = ApplicationBaseException.class, propagation = Propagation.REQUIRES_NEW)
    public UserDetails loadUserByUsernameAndCombination(String username, String combination) throws UsernameNotFoundException {
        Password temp = new Password();
        temp.setCombination(combination);
        Optional<Account> accountOptional = accountRepository.findByUsername(username);
        if (accountOptional.isEmpty()) {
            throw new UsernameNotFoundException(KEY_ACCOUNT_NOT_FOUND);
        } else {
            Account account = accountOptional.get();
            if (account.getPasswords().contains(temp)) {
                Password password = account.getPasswords().get(account.getPasswords().indexOf(temp));
                //TODO authorities
                return new CustomUserDetails(
                        username,
                        combination,
                        password.getHash(),
                        account.isConfirmed(),
                        true,
                        true, account.isActive(),
                        Collections.singletonList(new SimpleGrantedAuthority("USER")));
            } else {
                return null;
            }
        }
    }
}
