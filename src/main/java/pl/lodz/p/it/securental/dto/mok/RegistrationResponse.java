package pl.lodz.p.it.securental.dto.mok;

import lombok.Builder;
import lombok.Data;

@Builder
public @Data class RegistrationResponse {

    private String qrCode;
    private String lastPasswordCharacters;
}
