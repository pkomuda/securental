package pl.lodz.p.it.securental.dto.mok;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
public @Data class AccountDto {

    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private boolean active;
    private boolean confirmed;
    private List<String> accessLevels;
}
