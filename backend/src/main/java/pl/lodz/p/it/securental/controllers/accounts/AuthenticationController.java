package pl.lodz.p.it.securental.controllers.accounts;

import pl.lodz.p.it.securental.dto.accounts.AuthenticationRequest;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

import java.util.List;

public interface AuthenticationController {

    List<Integer> initializeLogin(String username) throws ApplicationBaseException;
    String login(AuthenticationRequest authRequest) throws ApplicationBaseException;
    void logout();
}
