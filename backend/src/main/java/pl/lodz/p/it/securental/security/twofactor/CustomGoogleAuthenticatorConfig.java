package pl.lodz.p.it.securental.security.twofactor;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.lodz.p.it.securental.adapters.accounts.TotpCredentialsAdapter;

@Configuration
@RequiredArgsConstructor
public class CustomGoogleAuthenticatorConfig {

    private final TotpCredentialsAdapter totpCredentialsAdapter;

    @Bean
    public GoogleAuthenticator gAuth() {
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
        googleAuthenticator.setCredentialRepository(totpCredentialsAdapter);
        return googleAuthenticator;
    }
}
