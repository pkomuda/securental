package pl.lodz.p.it.securental.security.model;

import lombok.Data;

public @Data class AuthenticationRequest {

    private String username;
    private String combination;
    private String characters;
}
