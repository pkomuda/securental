package pl.lodz.p.it.securental.dto.model.mok;

import lombok.Data;

public @Data class ChangePasswordRequest {

    private String password;

    private String confirmPassword;
}
