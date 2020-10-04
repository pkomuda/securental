package pl.lodz.p.it.securental.controllers.accounts.impl;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.p.it.securental.annotations.RequiresNewTransaction;
import pl.lodz.p.it.securental.controllers.accounts.AuthenticationController;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.accounts.IncorrectCredentialsException;
import pl.lodz.p.it.securental.security.AuthenticationRequest;
import pl.lodz.p.it.securental.security.CustomAuthenticationToken;
import pl.lodz.p.it.securental.security.CustomUserDetailsService;

import static pl.lodz.p.it.securental.utils.JwtUtils.generateToken;

@CrossOrigin
@RestController
@AllArgsConstructor
public class AuthenticationControllerImpl implements AuthenticationController {

    private final AuthenticationManager authManager;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping("/login")
    @RequiresNewTransaction
    public String login(@RequestBody AuthenticationRequest authRequest) throws ApplicationBaseException {
        UserDetails userDetails;
        try {
            authManager.authenticate(new CustomAuthenticationToken(
                    authRequest.getUsername(), authRequest.getCombination(), authRequest.getTotpCode(), authRequest.getCharacters())
            );
            userDetails = userDetailsService.loadUserByUsernameAndCombination(
                    authRequest.getUsername(), authRequest.getCombination()
            );
        } catch (AuthenticationException e) {
            throw new IncorrectCredentialsException(e);
        }

        return generateToken(userDetails);
    }

    public void logout() {
        throw new UnsupportedOperationException();
    }
}
