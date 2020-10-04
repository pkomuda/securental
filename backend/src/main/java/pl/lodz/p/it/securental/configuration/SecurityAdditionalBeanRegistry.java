package pl.lodz.p.it.securental.configuration;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.lodz.p.it.securental.adapters.accounts.TotpCredentialsAdapter;

import static pl.lodz.p.it.securental.utils.ApplicationProperties.APPLICATION_PROPERTIES_BEAN;
import static pl.lodz.p.it.securental.utils.ApplicationProperties.PASSWORD_ENCODER_STRENGTH;

@Configuration
@AllArgsConstructor
public class SecurityAdditionalBeanRegistry {

    private final TotpCredentialsAdapter totpCredentialsAdapter;

    @Bean
    @DependsOn(APPLICATION_PROPERTIES_BEAN)
    public PasswordEncoder passwordEncoderBean() {
        return new BCryptPasswordEncoder(PASSWORD_ENCODER_STRENGTH);
    }

    @Bean
    public GoogleAuthenticator googleAuthenticatorBean() {
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
        googleAuthenticator.setCredentialRepository(totpCredentialsAdapter);
        return googleAuthenticator;
    }
}
