package pl.lodz.p.it.securental.controllers.mok.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.it.securental.annotations.NeverTransaction;
import pl.lodz.p.it.securental.controllers.mok.AuthenticationController;
import pl.lodz.p.it.securental.dto.mok.AuthenticationRequest;
import pl.lodz.p.it.securental.dto.mok.AuthenticationResponse;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.mok.IncorrectCredentialsException;
import pl.lodz.p.it.securental.security.CustomAuthenticationToken;
import pl.lodz.p.it.securental.security.CustomUserDetailsService;
import pl.lodz.p.it.securental.services.mok.AccountService;
import pl.lodz.p.it.securental.utils.JwtUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static pl.lodz.p.it.securental.utils.ApplicationProperties.*;
import static pl.lodz.p.it.securental.utils.JwtUtils.*;
import static pl.lodz.p.it.securental.utils.StringUtils.integerArrayToString;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@AllArgsConstructor
@NeverTransaction
public class AuthenticationControllerImpl implements AuthenticationController {

    private final AuthenticationManager authManager;
    private final CustomUserDetailsService userDetailsService;
    private final AccountService accountService;

    @Override
    @GetMapping("/initializeLogin/{username}")
    public List<Integer> initializeLogin(@PathVariable String username) throws ApplicationBaseException {
        return accountService.initializeLogin(username);
    }

    @Override
    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest authRequest,
                                        HttpServletResponse response) throws ApplicationBaseException {
        UserDetails userDetails;
        try {
            authManager.authenticate(new CustomAuthenticationToken(
                    authRequest.getUsername(),
                    integerArrayToString(authRequest.getCombination()),
                    authRequest.getOtpCode(),
                    authRequest.getCharacters())
            );
            userDetails = userDetailsService.loadUserByUsernameAndCombination(
                    authRequest.getUsername(),
                    integerArrayToString(authRequest.getCombination())
            );
        } catch (AuthenticationException e) {
            throw new IncorrectCredentialsException(e);
        }

        String jwt = generateToken(userDetails);
        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * JWT_EXPIRATION_TIME);
        response.addCookie(cookie);

        List<String> accessLevels = getAccessLevels(userDetails);
        return AuthenticationResponse.builder()
                .username(userDetails.getUsername())
                .accessLevels(accessLevels)
                .currentAccessLevel(getHighestAccessLevel(accessLevels))
                .tokenExpiration(JwtUtils.extractExpiration(jwt).getTime())
                .build();
    }

    @Override
    public void logout() {
        throw new UnsupportedOperationException();
    }

    @GetMapping("/currentUser")
    @PreAuthorize("hasAuthority('currentUser')")
    public AuthenticationResponse currentUser(HttpServletRequest request) throws ApplicationBaseException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> cookieOptional = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("jwt"))
                    .findFirst();
            if (cookieOptional.isPresent()) {
                Cookie cookie = cookieOptional.get();
                return accountService.currentUser(extractUsername(cookie.getValue()))
                        .tokenExpiration(extractExpiration(cookie.getValue()).getTime())
                        .build();
            } else {
                return AuthenticationResponse.builder()
                        .username("")
                        .accessLevels(Collections.emptyList())
                        .tokenExpiration(0)
                        .build();
            }
        } else {
            return AuthenticationResponse.builder()
                    .username("")
                    .accessLevels(Collections.emptyList())
                    .tokenExpiration(0)
                    .build();
        }
    }

    @GetMapping("/employee")
    @PreAuthorize("hasAuthority('employeeTest')")
    public void employeeTest(HttpServletRequest request) {
        log.info("employee");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('adminTest')")
    public void adminTest(HttpServletRequest request) {
        log.info("admin");
    }

    private List<String> getAccessLevels(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    private String getHighestAccessLevel(List<String> accessLevels) {
        if (accessLevels.contains(ACCESS_LEVEL_ADMIN)) {
            return ACCESS_LEVEL_ADMIN;
        } else if (accessLevels.contains(ACCESS_LEVEL_EMPLOYEE)) {
            return ACCESS_LEVEL_EMPLOYEE;
        } else {
            return ACCESS_LEVEL_CLIENT;
        }
    }
}
