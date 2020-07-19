package pl.lodz.p.it.securental.controllers.accounts.impl;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.p.it.securental.controllers.accounts.AuthenticationController;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.accounts.IncorrectCredentialsException;
import pl.lodz.p.it.securental.security.AuthenticationRequest;
import pl.lodz.p.it.securental.security.AuthenticationResponse;
import pl.lodz.p.it.securental.security.CustomAuthenticationToken;
import pl.lodz.p.it.securental.utils.JwtUtils;

import java.util.ArrayList;

@RestController
@AllArgsConstructor
@Transactional(rollbackFor = ApplicationBaseException.class, propagation = Propagation.NEVER)
public class AuthenticationControllerImpl implements AuthenticationController {

    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;

    @Override
    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest authRequest) throws ApplicationBaseException {
        try {
            authManager.authenticate(new CustomAuthenticationToken(authRequest.getUsername(), authRequest.getCombination(), authRequest.getCharacters(), new ArrayList<>()));
        } catch (BadCredentialsException e) {
            throw new IncorrectCredentialsException(e);
        }

        String jwt = jwtUtils.generateToken(new User(authRequest.getUsername(), authRequest.getCharacters(), new ArrayList<>()));
        return new AuthenticationResponse(jwt);
    }

    @Override
    public void logout() throws ApplicationBaseException {
        throw new UnsupportedOperationException();
    }
}
