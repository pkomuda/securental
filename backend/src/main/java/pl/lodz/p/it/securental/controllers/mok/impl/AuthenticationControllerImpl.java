package pl.lodz.p.it.securental.controllers.mok.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static pl.lodz.p.it.securental.utils.JwtUtils.extractUsername;
import static pl.lodz.p.it.securental.utils.JwtUtils.generateToken;
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

        Cookie cookie = new Cookie("jwt", generateToken(userDetails));
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return AuthenticationResponse.builder()
                .username(userDetails.getUsername())
                .accessLevels(getAccessLevels(userDetails))
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
                return accountService.currentUser(extractUsername(cookieOptional.get().getValue()));
            } else {
                return AuthenticationResponse.builder()
                        .username("")
                        .accessLevels(Collections.emptyList())
                        .build();
            }
        } else {
            return AuthenticationResponse.builder()
                    .username("")
                    .accessLevels(Collections.emptyList())
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
}
