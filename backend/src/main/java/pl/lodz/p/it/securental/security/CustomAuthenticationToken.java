package pl.lodz.p.it.securental.security;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private String combination;

    public CustomAuthenticationToken(Object principal, String combination, Object credentials) {
        super(principal, credentials);
        this.combination = combination;
        super.setAuthenticated(false);
    }

    public CustomAuthenticationToken(Object principal, String combination, Object credentials,
                                     Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        this.combination = combination;
        super.setAuthenticated(true); // must use super, as we override
    }
}
