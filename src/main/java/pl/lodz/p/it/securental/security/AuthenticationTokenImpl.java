package pl.lodz.p.it.securental.security;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
public class AuthenticationTokenImpl extends UsernamePasswordAuthenticationToken {

    private final Integer otpCode;

    public AuthenticationTokenImpl(Object principal, Integer otpCode, Object credentials) {
        super(principal, credentials);
        this.otpCode = otpCode;
        super.setAuthenticated(false);
    }
}
