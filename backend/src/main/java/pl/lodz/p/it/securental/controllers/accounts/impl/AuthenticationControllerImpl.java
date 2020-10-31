package pl.lodz.p.it.securental.controllers.accounts.impl;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.it.securental.annotations.NeverTransaction;
import pl.lodz.p.it.securental.controllers.accounts.AuthenticationController;
import pl.lodz.p.it.securental.dto.accounts.AuthenticationRequest;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.accounts.IncorrectCredentialsException;
import pl.lodz.p.it.securental.security.CustomAuthenticationToken;
import pl.lodz.p.it.securental.security.CustomUserDetailsService;
import pl.lodz.p.it.securental.services.accounts.AccountService;

import java.util.List;

import static pl.lodz.p.it.securental.utils.JwtUtils.generateToken;
import static pl.lodz.p.it.securental.utils.StringUtils.integerArrayToString;

@CrossOrigin
@RestController
@AllArgsConstructor
@NeverTransaction
public class AuthenticationControllerImpl implements AuthenticationController {

    private final AuthenticationManager authManager;
    private final CustomUserDetailsService userDetailsService;
    private final AccountService accountService;

    @Override
    @GetMapping("/initializeLogin/{username}")
    public List<Integer> initializeLogin(@PathVariable String username) throws ApplicationBaseException {
        return accountService.initializeLogin(username);
    }

    @Override
    @PostMapping("/login")
    public String login(@RequestBody AuthenticationRequest authRequest) throws ApplicationBaseException {
        UserDetails userDetails;
        try {
            authManager.authenticate(new CustomAuthenticationToken(
                    authRequest.getUsername(),
                    integerArrayToString(authRequest.getCombination()),
                    authRequest.getTotpCode(),
                    authRequest.getCharacters())
            );
            userDetails = userDetailsService.loadUserByUsernameAndCombination(
                    authRequest.getUsername(),
                    integerArrayToString(authRequest.getCombination())
            );
        } catch (AuthenticationException e) {
            throw new IncorrectCredentialsException(e);
        }

        return generateToken(userDetails);
    }

    @Override
    public void logout() {
        throw new UnsupportedOperationException();
    }
}
