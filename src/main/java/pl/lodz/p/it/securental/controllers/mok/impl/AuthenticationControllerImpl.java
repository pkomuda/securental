package pl.lodz.p.it.securental.controllers.mok.impl;

import lombok.AllArgsConstructor;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.it.securental.aop.annotations.NeverTransaction;
import pl.lodz.p.it.securental.controllers.mok.AuthenticationController;
import pl.lodz.p.it.securental.dto.model.mok.AuthenticationRequest;
import pl.lodz.p.it.securental.dto.model.mok.AuthenticationResponse;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.db.DatabaseConnectionException;
import pl.lodz.p.it.securental.exceptions.mok.IncorrectCredentialsException;
import pl.lodz.p.it.securental.security.AuthenticationTokenImpl;
import pl.lodz.p.it.securental.security.UserDetailsServiceImpl;
import pl.lodz.p.it.securental.services.mok.AuthenticationService;
import pl.lodz.p.it.securental.utils.ApplicationProperties;
import pl.lodz.p.it.securental.utils.JwtUtils;
import pl.lodz.p.it.securental.utils.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@NeverTransaction
@Retryable(DatabaseConnectionException.class)
public class AuthenticationControllerImpl implements AuthenticationController {

    private final AuthenticationManager authManager;
    private final UserDetailsServiceImpl userDetailsService;
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
    public AuthenticationResponse login(@RequestBody AuthenticationRequest authRequest,
                                        HttpServletResponse response) throws ApplicationBaseException {
        try {
            authManager.authenticate(new AuthenticationTokenImpl(
                    authRequest.getUsername(),
                    StringUtils.integerArrayToString(authRequest.getCombination()),
                    authRequest.getOtpCode(),
                    authRequest.getCharacters())
            );
            UserDetails userDetails = userDetailsService.loadUserByUsernameAndCombination(
                    authRequest.getUsername(),
                    StringUtils.integerArrayToString(authRequest.getCombination())
            );

            String jwt = JwtUtils.generateToken(userDetails);
            Cookie cookie = new Cookie("Authorization", jwt);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(60 * ApplicationProperties.JWT_EXPIRATION_TIME);
            response.addCookie(cookie);
            if (ApplicationProperties.isProduction()) {
                response.setHeader("Set-Cookie", response.getHeader("Set-Cookie") + "; SameSite=Strict; Secure");
            }

            List<String> accessLevels = getAccessLevels(userDetails);
            return authenticationService.finalizeLogin(authRequest.getUsername(), true)
                    .username(authRequest.getUsername())
                    .accessLevels(accessLevels)
                    .currentAccessLevel(getHighestAccessLevel(accessLevels))
                    .tokenExpiration(JwtUtils.extractExpiration(jwt).getTime())
                    .build();
        } catch (AuthenticationException e1) {
            try {
                authenticationService.finalizeLogin(authRequest.getUsername(), false);
            } catch (ApplicationBaseException e2) {
                throw new IncorrectCredentialsException(e2);
            }
            throw new IncorrectCredentialsException(e1);
        }
    }

    @Override
    @GetMapping("/currentUser")
    @PreAuthorize("hasAuthority('currentUser')")
    public AuthenticationResponse currentUser(HttpServletRequest request) throws ApplicationBaseException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> cookieOptional = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("Authorization"))
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
    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('logout')")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws ApplicationBaseException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> cookieOptional = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("Authorization"))
                    .findFirst();
            if (cookieOptional.isPresent()) {
                Cookie cookie = cookieOptional.get();
                Cookie empty = new Cookie("Authorization", "");
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

    private List<String> getAccessLevels(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .sorted(Comparator.comparing(ApplicationProperties.ACCESS_LEVEL_ORDER::indexOf))
                .collect(Collectors.toList());
    }

    private String getHighestAccessLevel(List<String> accessLevels) {
        if (accessLevels.contains(ApplicationProperties.ACCESS_LEVEL_ADMIN)) {
            return ApplicationProperties.ACCESS_LEVEL_ADMIN;
        } else if (accessLevels.contains(ApplicationProperties.ACCESS_LEVEL_EMPLOYEE)) {
            return ApplicationProperties.ACCESS_LEVEL_EMPLOYEE;
        } else {
            return ApplicationProperties.ACCESS_LEVEL_CLIENT;
        }
    }
}
