package pl.lodz.p.it.securental.controllers.mok;

import pl.lodz.p.it.securental.dto.mok.AuthenticationRequest;
import pl.lodz.p.it.securental.dto.mok.AuthenticationResponse;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface AuthenticationController {

    List<Integer> initializeLogin(String username) throws ApplicationBaseException;
    AuthenticationResponse login(AuthenticationRequest authRequest, HttpServletResponse response) throws ApplicationBaseException;
    void logout();
}
