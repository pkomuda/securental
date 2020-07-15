package pl.lodz.p.it.securental.dto.accounts;

import lombok.Data;

public @Data class AccountDto {

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean active;
}
