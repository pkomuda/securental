package pl.lodz.p.it.securental.controllers.mok;

import pl.lodz.p.it.securental.dto.mok.AuthenticationRequest;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

import java.util.List;

public interface AuthenticationController {

    List<Integer> initializeLogin(String username) throws ApplicationBaseException;
    String login(AuthenticationRequest authRequest) throws ApplicationBaseException;
    void logout();
}
