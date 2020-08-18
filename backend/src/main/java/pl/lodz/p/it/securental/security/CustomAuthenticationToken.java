package pl.lodz.p.it.securental.security;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final String combination;

    public CustomAuthenticationToken(Object principal, String combination, Object credentials) {
        super(principal, credentials);
        this.combination = combination;
        super.setAuthenticated(false);
    }
}
