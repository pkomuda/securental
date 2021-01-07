package pl.lodz.p.it.securental.dto.mok;

import lombok.Builder;
import lombok.Data;

@Builder
public @Data class ClientDto {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
}
