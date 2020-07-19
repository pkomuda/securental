package pl.lodz.p.it.securental.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class CustomAuthenticationToken extends AbstractAuthenticationToken {

    private final String principal;
    private String combination;
    private String characters;

    public CustomAuthenticationToken(String principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
    }

    public CustomAuthenticationToken(String principal, String combination, String characters, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.combination = combination;
        this.characters = characters;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public Object getCredentials() {
        return this.characters;
    }
}
