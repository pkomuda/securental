package pl.lodz.p.it.securental.dto.accounts;

import lombok.Data;

public @Data class AuthenticationRequest {

    private String username;
    private int[] combination;
    private Integer totpCode;
    private String characters;
}
