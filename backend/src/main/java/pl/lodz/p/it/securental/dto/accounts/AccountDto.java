package pl.lodz.p.it.securental.dto.accounts;

import lombok.Builder;
import lombok.Data;

@Builder
public @Data class AccountDto {

    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private boolean active;
    private boolean confirmed;
}
