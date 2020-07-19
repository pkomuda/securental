package pl.lodz.p.it.securental.security;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.lodz.p.it.securental.entities.accounts.Account;
import pl.lodz.p.it.securental.entities.accounts.Password;
import pl.lodz.p.it.securental.repositories.accounts.AccountRepository;

import java.util.ArrayList;
import java.util.Optional;

import static pl.lodz.p.it.securental.exceptions.accounts.AccountNotFoundException.KEY_ACCOUNT_NOT_FOUND;

@Component
@AllArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    public static final String KEY_INCORRECT_CREDENTIALS = "error.credentials";

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String username = auth.getName();
        String combination = ((CustomAuthenticationToken) auth).getCombination();
        String characters = ((CustomAuthenticationToken) auth).getCharacters();

        Optional<Account> accountOptional = accountRepository.findByUsername(username);
        if (accountOptional.isEmpty()) {
            throw new BadCredentialsException(KEY_ACCOUNT_NOT_FOUND);
        }

        //TODO TOTP

        Account account = accountOptional.get();
        Password password = new Password();
        password.setCombination(combination);
        if (passwordEncoder.matches(characters, account.getPasswords().get(account.getPasswords().indexOf(password)).getHash())) {
            return new CustomAuthenticationToken(username, new ArrayList<>());
        } else {
            throw new BadCredentialsException(KEY_INCORRECT_CREDENTIALS);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(CustomAuthenticationToken.class);
    }
}
