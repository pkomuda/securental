package pl.lodz.p.it.securental.security;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final String combination;
    private final Integer totpCode;

    public CustomAuthenticationToken(Object principal, String combination, Integer totpCode, Object credentials) {
        super(principal, credentials);
        this.combination = combination;
        this.totpCode = totpCode;
        super.setAuthenticated(false);
    }
}
