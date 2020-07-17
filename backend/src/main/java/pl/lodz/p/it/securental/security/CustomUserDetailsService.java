package pl.lodz.p.it.securental.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.repositories.accounts.AccountRepository;

import java.util.ArrayList;

import static pl.lodz.p.it.securental.exceptions.accounts.AccountNotFoundException.KEY_ACCOUNT_NOT_FOUND;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (accountRepository.findByUsername(username).isPresent()) {
            return new User(username, null, new ArrayList<>());
        } else {
            throw new UsernameNotFoundException(KEY_ACCOUNT_NOT_FOUND);
        }
    }
}
