package pl.lodz.p.it.securental.security;

import lombok.Data;

public @Data class AuthenticationRequest {

    private String username;
    private String combination;
    private Integer totpCode;
    private String characters;
}