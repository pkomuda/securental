package pl.lodz.p.it.securental.controllers.accounts;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.security.AuthenticationRequest;
import pl.lodz.p.it.securental.security.AuthenticationResponse;

public interface AuthenticationController {

    AuthenticationResponse login(AuthenticationRequest authRequest) throws ApplicationBaseException;
    void logout() throws ApplicationBaseException;
}
