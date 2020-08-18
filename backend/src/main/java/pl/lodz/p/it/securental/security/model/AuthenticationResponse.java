package pl.lodz.p.it.securental.security.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public @Data class AuthenticationResponse {

    private String jwt;
}
