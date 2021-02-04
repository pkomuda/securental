package pl.lodz.p.it.securental.dto.model.mok;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public @Data class AuthenticationResponse {

    private String username;
    private List<String> accessLevels;
    private String currentAccessLevel;
    private long tokenExpiration;
    private String lastSuccessfulAuthentication;
    private String lastFailedAuthentication;
    private String lastAuthenticationIpAddress;

    public AuthenticationResponse unauthenticated() {
        username = "";
        accessLevels = Collections.emptyList();
        currentAccessLevel = "";
        tokenExpiration = 0;
        lastSuccessfulAuthentication = "";
        lastFailedAuthentication = "";
        lastAuthenticationIpAddress = "";
        return this;
    }
}