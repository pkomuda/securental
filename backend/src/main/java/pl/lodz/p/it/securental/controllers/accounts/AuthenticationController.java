package pl.lodz.p.it.securental.controllers.accounts;

import org.springframework.http.ResponseEntity;
import pl.lodz.p.it.securental.security.old.AuthenticationRequest;

public interface AuthenticationController {

    ResponseEntity<?> login(AuthenticationRequest authRequest);
    void logout();
}
