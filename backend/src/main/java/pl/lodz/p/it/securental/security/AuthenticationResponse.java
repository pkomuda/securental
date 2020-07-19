package pl.lodz.p.it.securental.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public @Data class AuthenticationResponse {

    private String jwt;
}
