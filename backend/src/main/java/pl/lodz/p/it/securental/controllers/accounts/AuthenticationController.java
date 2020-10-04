package pl.lodz.p.it.securental.controllers.accounts;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.security.AuthenticationRequest;

public interface AuthenticationController {

    String login(AuthenticationRequest authRequest) throws ApplicationBaseException;
    void logout();
}
