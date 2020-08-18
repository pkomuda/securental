package pl.lodz.p.it.securental.controllers.accounts.impl;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.p.it.securental.security.CustomAuthenticationToken;
import pl.lodz.p.it.securental.security.CustomUserDetailsService;
import pl.lodz.p.it.securental.security.old.AuthenticationResponse;
import pl.lodz.p.it.securental.security.old.JwtService;

@CrossOrigin
@RestController
@AllArgsConstructor
public class AuthenticationControllerImpl {

    private final AuthenticationManager authManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    @PostMapping("/login")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String combination, @RequestParam String password) {
        try {
            authManager.authenticate(new CustomAuthenticationToken(username, combination, password));
        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Incorrect credentials.");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsernameAndCombination(username, combination);
        String jwt = jwtService.generateToken(userDetails);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new AuthenticationResponse(jwt));
    }

    public void logout() {
        throw new UnsupportedOperationException();
    }
}
