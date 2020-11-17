package pl.lodz.p.it.securental.dto.mok;

import lombok.Data;

public @Data class AuthenticationRequest {

    private String username;
    private int[] combination;
    private Integer otpCode;
    private String characters;
}
