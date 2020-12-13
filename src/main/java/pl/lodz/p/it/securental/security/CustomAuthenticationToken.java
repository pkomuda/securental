package pl.lodz.p.it.securental.security;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final String combination;
    private final Integer otpCode;

    public CustomAuthenticationToken(Object principal, String combination, Integer otpCode, Object credentials) {
        super(principal, credentials);
        this.combination = combination;
        this.otpCode = otpCode;
        super.setAuthenticated(false);
    }
}
