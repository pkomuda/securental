package pl.lodz.p.it.securental.dto.mok;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
public @Data class AuthenticationResponse {

    private String username;
    private List<String> accessLevels;
    private String currentAccessLevel;
    private long tokenExpiration;
}
