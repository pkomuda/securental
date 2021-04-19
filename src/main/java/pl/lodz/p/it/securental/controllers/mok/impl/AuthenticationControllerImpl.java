package pl.lodz.p.it.securental.controllers.mok.impl;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.it.securental.aop.annotations.NeverTransaction;
import pl.lodz.p.it.securental.aop.annotations.OtpAuthorizationRequired;
import pl.lodz.p.it.securental.controllers.mok.AuthenticationController;
import pl.lodz.p.it.securental.dto.model.mok.AuthenticationRequest;
import pl.lodz.p.it.securental.dto.model.mok.AuthenticationResponse;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.mok.AuthenticationFailedException;
import pl.lodz.p.it.securental.security.AuthenticationTokenImpl;
import pl.lodz.p.it.securental.services.mok.AuthenticationService;
import pl.lodz.p.it.securental.utils.ApplicationProperties;
import pl.lodz.p.it.securental.utils.JwtUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@NeverTransaction
public class AuthenticationControllerImpl implements AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final AuthenticationService authenticationService;

    @Override
    @GetMapping("/initializeLogin/{username}")
    @PreAuthorize("permitAll()")
    public List<Integer> initializeLogin(@PathVariable String username) throws ApplicationBaseException {
        return authenticationService.initializeLogin(username);
    }

    @Override
    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest authenticationRequest,
                                        HttpServletRequest request,
                                        HttpServletResponse response) throws ApplicationBaseException {
        try {
            authenticationManager.authenticate(new AuthenticationTokenImpl(
                    authenticationRequest.getUsername(),
                    authenticationRequest.getOtpCode(),
                    authenticationRequest.getCharacters())
            );

            String jwt = JwtUtils.generateToken(authenticationRequest.getUsername());
            Cookie cookie = new Cookie("Token", jwt);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(60 * ApplicationProperties.JWT_EXPIRATION_TIME);
            response.addCookie(cookie);
            if (ApplicationProperties.isProduction()) {
                response.setHeader("Set-Cookie", response.getHeader("Set-Cookie") + "; SameSite=Strict; Secure");
            }

            return authenticationService.finalizeLogin(authenticationRequest.getUsername(), request.getRemoteAddr(), true)
                    .tokenExpiration(JwtUtils.extractExpiration(jwt).getTime())
                    .build();
        } catch (AuthenticationException e1) {
            try {
                authenticationService.finalizeLogin(authenticationRequest.getUsername(), "", false);
            } catch (ApplicationBaseException e2) {
                throw new AuthenticationFailedException(e2);
            }
            throw new AuthenticationFailedException(e1);
        }
    }

    @Override
    @GetMapping("/currentUser")
    @PreAuthorize("hasAuthority('currentUser')")
    public AuthenticationResponse currentUser(HttpServletRequest request) throws ApplicationBaseException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> cookieOptional = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("Token"))
                    .findFirst();
            if (cookieOptional.isPresent()) {
                Cookie cookie = cookieOptional.get();
                return authenticationService.currentUser(JwtUtils.extractUsername(cookie.getValue()))
                        .tokenExpiration(JwtUtils.extractExpiration(cookie.getValue()).getTime())
                        .build();
            } else {
                return new AuthenticationResponse().unauthenticated();
            }
        } else {
            return new AuthenticationResponse().unauthenticated();
        }
    }

    @Override
    @OtpAuthorizationRequired
    @PostMapping("/refresh")
    @PreAuthorize("hasAuthority('refreshSession')")
    public void refreshSession(HttpServletResponse response) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String jwt = JwtUtils.generateToken(username);
        Cookie cookie = new Cookie("Token", jwt);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * ApplicationProperties.JWT_EXPIRATION_TIME);
        response.addCookie(cookie);
        if (ApplicationProperties.isProduction()) {
            response.setHeader("Set-Cookie", response.getHeader("Set-Cookie") + "; SameSite=Strict; Secure");
        }
    }

    @Override
    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('logout')")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws ApplicationBaseException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> cookieOptional = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("Token"))
                    .findFirst();
            if (cookieOptional.isPresent()) {
                Cookie cookie = cookieOptional.get();
                Cookie empty = new Cookie("Token", "");
                empty.setHttpOnly(true);
                empty.setMaxAge(0);
                response.addCookie(empty);
                if (ApplicationProperties.isProduction()) {
                    response.setHeader("Set-Cookie", response.getHeader("Set-Cookie") + "; SameSite=Strict; Secure");
                }
                authenticationService.addJwtToBlacklist(cookie.getValue(), JwtUtils.extractExpiration(cookie.getValue()).getTime());
            }
        }
    }
}
