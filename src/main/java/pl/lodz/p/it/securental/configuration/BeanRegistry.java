package pl.lodz.p.it.securental.configuration;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.AllArgsConstructor;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.lodz.p.it.securental.adapters.mok.OtpCredentialsAdapter;
import pl.lodz.p.it.securental.utils.ApplicationProperties;

@Configuration
@AllArgsConstructor
@DependsOn(ApplicationProperties.APPLICATION_PROPERTIES_BEAN)
public class BeanRegistry {

    private final OtpCredentialsAdapter otpCredentialsAdapter;

    @Bean
    public PasswordEncoder passwordEncoderBean() {
        return new MessageDigestPasswordEncoder(ApplicationProperties.PASSWORD_HASHING_ALGORITHM);
    }

    @Bean
    public GoogleAuthenticator googleAuthenticatorBean() {
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
        googleAuthenticator.setCredentialRepository(otpCredentialsAdapter);
        return googleAuthenticator;
    }

    @Bean
    public Cache<String, String> logCacheBean() {
        DefaultCacheManager cacheManager = new DefaultCacheManager();
        cacheManager.defineConfiguration(ApplicationProperties.LOG_CACHE_NAME, new ConfigurationBuilder().build());
        return cacheManager.getCache(ApplicationProperties.LOG_CACHE_NAME);
    }
}
