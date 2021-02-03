package pl.lodz.p.it.securental.controllers.mok;

import pl.lodz.p.it.securental.dto.model.mok.AuthenticationRequest;
import pl.lodz.p.it.securental.dto.model.mok.AuthenticationResponse;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface AuthenticationController {

    List<Integer> initializeLogin(String username) throws ApplicationBaseException;
    AuthenticationResponse login(AuthenticationRequest authRequest, HttpServletRequest request, HttpServletResponse response) throws ApplicationBaseException;
    AuthenticationResponse currentUser(HttpServletRequest request) throws ApplicationBaseException;
    void logout(HttpServletRequest request, HttpServletResponse response) throws ApplicationBaseException;
}
