package pl.lodz.p.it.securental.security;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.it.securental.entities.accounts.Account;
import pl.lodz.p.it.securental.entities.accounts.Password;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.repositories.accounts.AccountRepository;

import java.util.ArrayList;
import java.util.Optional;

import static pl.lodz.p.it.securental.exceptions.accounts.IncorrectCredentialsException.KEY_INCORRECT_CREDENTIALS;

@Component
@AllArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = ApplicationBaseException.class, propagation = Propagation.REQUIRES_NEW)
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String username = auth.getName();
        String combination = ((CustomAuthenticationToken) auth).getCombination();
        String characters = ((CustomAuthenticationToken) auth).getCharacters();

        Optional<Account> accountOptional = accountRepository.findByUsername(username);
        if (accountOptional.isEmpty()) {
            throw new BadCredentialsException(KEY_INCORRECT_CREDENTIALS);
        }

        //TODO TOTP

        Account account = accountOptional.get();
        Password password = new Password();
        password.setCombination(combination);
        if (passwordEncoder.matches(characters,
                account.getPasswords().get(account.getPasswords().indexOf(password)).getHash())) {
            CustomAuthenticationToken customAuthenticationToken = new CustomAuthenticationToken(username, new ArrayList<>());
//            customAuthenticationToken.setAuthenticated(true);
            return customAuthenticationToken;
        } else {
            throw new BadCredentialsException(KEY_INCORRECT_CREDENTIALS);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(CustomAuthenticationToken.class);
    }
}
