package pl.lodz.p.it.securental.configuration;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.lodz.p.it.securental.adapters.accounts.OtpCredentialsAdapter;

import static pl.lodz.p.it.securental.utils.ApplicationProperties.APPLICATION_PROPERTIES_BEAN;
import static pl.lodz.p.it.securental.utils.ApplicationProperties.PASSWORD_HASHING_ALGORITHM;

@Configuration
@AllArgsConstructor
public class BeanRegistry {

    private final OtpCredentialsAdapter otpCredentialsAdapter;

    @Bean
    @DependsOn(APPLICATION_PROPERTIES_BEAN)
    public PasswordEncoder passwordEncoderBean() {
        return new MessageDigestPasswordEncoder(PASSWORD_HASHING_ALGORITHM);
    }

    @Bean
    public GoogleAuthenticator googleAuthenticatorBean() {
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
        googleAuthenticator.setCredentialRepository(otpCredentialsAdapter);
        return googleAuthenticator;
    }
}
