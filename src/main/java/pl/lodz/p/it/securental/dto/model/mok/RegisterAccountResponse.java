package pl.lodz.p.it.securental.dto.model.mok;

import lombok.Data;

public @Data class RegisterAccountResponse {

    private String qrCode;
    private String lastPasswordCharacters;
}
