package pl.lodz.p.it.securental.dto.model.mok;

import lombok.Data;

public @Data class AuthenticationRequest {

    private String username;
    private String characters;
    private Integer otpCode;
}
