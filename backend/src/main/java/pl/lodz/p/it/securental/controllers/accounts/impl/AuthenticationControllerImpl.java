package pl.lodz.p.it.securental.controllers.accounts.impl;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.p.it.securental.annotations.NeverTransaction;
import pl.lodz.p.it.securental.controllers.accounts.AuthenticationController;
import pl.lodz.p.it.securental.security.CustomAuthenticationToken;
import pl.lodz.p.it.securental.security.CustomUserDetailsService;
import pl.lodz.p.it.securental.security.model.AuthenticationRequest;
import pl.lodz.p.it.securental.security.model.AuthenticationResponse;
import pl.lodz.p.it.securental.utils.JwtUtils;

@CrossOrigin
@RestController
@AllArgsConstructor
@NeverTransaction
public class AuthenticationControllerImpl implements AuthenticationController {

    private final AuthenticationManager authManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authRequest) {
        UserDetails userDetails;
        try {
            authManager.authenticate(new CustomAuthenticationToken(
                    authRequest.getUsername(), authRequest.getCombination(), authRequest.getCharacters())
            );
            userDetails = userDetailsService.loadUserByUsernameAndCombination(
                    authRequest.getUsername(), authRequest.getCombination()
            );
        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Incorrect credentials.");
        }

        String jwt = jwtUtils.generateToken(userDetails);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new AuthenticationResponse(jwt));
    }

    public void logout() {
        throw new UnsupportedOperationException();
    }
}
