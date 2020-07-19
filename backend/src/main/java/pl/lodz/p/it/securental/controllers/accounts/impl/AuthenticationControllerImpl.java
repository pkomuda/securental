package pl.lodz.p.it.securental.controllers.accounts.impl;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.p.it.securental.controllers.accounts.AuthenticationController;
import pl.lodz.p.it.securental.security.AuthenticationRequest;
import pl.lodz.p.it.securental.security.AuthenticationResponse;
import pl.lodz.p.it.securental.security.CustomAuthenticationToken;
import pl.lodz.p.it.securental.security.JwtService;

import java.util.ArrayList;

@CrossOrigin
@RestController
@AllArgsConstructor
public class AuthenticationControllerImpl implements AuthenticationController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    @Override
    @PostMapping("/login")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authRequest) {
        try {
            authManager.authenticate(new CustomAuthenticationToken(authRequest.getUsername(), authRequest.getCombination(), authRequest.getCharacters(), new ArrayList<>()));
        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Incorrect credentials.");
        }

        AuthenticationResponse authResponse = new AuthenticationResponse();
        String jwt = jwtService.generateToken(new User(authRequest.getUsername(), authRequest.getCharacters(), new ArrayList<>()));
        authResponse.setJwt(jwt);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authResponse);
    }

    @Override
    public void logout() {
        throw new UnsupportedOperationException();
    }
}
