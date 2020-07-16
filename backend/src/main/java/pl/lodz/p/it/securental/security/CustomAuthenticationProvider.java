package pl.lodz.p.it.securental.security;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.lodz.p.it.securental.entities.accounts.Account;
import pl.lodz.p.it.securental.entities.accounts.Password;
import pl.lodz.p.it.securental.repositories.accounts.AccountRepository;

import java.util.ArrayList;
import java.util.Optional;

import static pl.lodz.p.it.securental.exceptions.ApplicationBaseException.KEY_DEFAULT;
import static pl.lodz.p.it.securental.exceptions.accounts.AccountNotFoundException.KEY_ACCOUNT_NOT_FOUND;

@Component
@AllArgsConstructor
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String username = auth.getName();
        String combination = ((CustomWebAuthenticationDetails) auth.getDetails()).getCombination();
        String characters = ((CustomWebAuthenticationDetails) auth.getDetails()).getCharacters();

        Optional<Account> accountOptional = accountRepository.findByUsername(username);
        if (accountOptional.isEmpty()) {
            throw new BadCredentialsException(KEY_ACCOUNT_NOT_FOUND);
        }

        //TODO TOTP

        Account account = accountOptional.get();
        Password password = new Password();
        password.setCombination(combination);
        if (passwordEncoder.matches(characters, account.getPasswords().get(account.getPasswords().indexOf(password)).getHash())) {
            return new UsernamePasswordAuthenticationToken(username, characters, new ArrayList<>()); //2 argument?
        } else {
            throw new BadCredentialsException(KEY_DEFAULT);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
