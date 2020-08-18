package pl.lodz.p.it.securental.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public static final String SPRING_SECURITY_FORM_COMBINATION_KEY = "combination";

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: "
                    + request.getMethod());
        }

        CustomAuthenticationToken authRequest = getAuthRequest(request);
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private CustomAuthenticationToken getAuthRequest(HttpServletRequest request) {
        String username = obtainUsername(request);
        String combination = obtainCombination(request);
        String password = obtainPassword(request);


        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        if (combination == null) {
            combination = "";
        }

        return new CustomAuthenticationToken(username, combination, password);
    }

    private String obtainCombination(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_COMBINATION_KEY);
    }
}
