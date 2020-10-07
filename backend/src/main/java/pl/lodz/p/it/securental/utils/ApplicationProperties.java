package pl.lodz.p.it.securental.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component(ApplicationProperties.APPLICATION_PROPERTIES_BEAN)
public class ApplicationProperties {

    public static final String APPLICATION_PROPERTIES_BEAN = "applicationProperties";

    public static String APPLICATION_NAME;
    public static String FRONTEND_ORIGIN;
    public static Integer FULL_PASSWORD_LENGTH;
    public static String JWT_KEY;
    public static Integer MASKED_PASSWORD_MAX_LENGTH;
    public static Integer MASKED_PASSWORD_MIN_LENGTH;
    public static String PASSWORD_HASHING_ALGORITHM;

    public ApplicationProperties(@Value("${APPLICATION_NAME}") String applicationName,
                                 @Value("${FRONTEND_ORIGIN}") String frontendOrigin,
                                 @Value("${FULL_PASSWORD_LENGTH}") Integer fullPasswordLength,
                                 @Value("${JWT_KEY}") String jwtKey,
                                 @Value("${MASKED_PASSWORD_MAX_LENGTH}") Integer maskedPasswordMaxLength,
                                 @Value("${MASKED_PASSWORD_MIN_LENGTH}") Integer maskedPasswordMinLength,
                                 @Value("${PASSWORD_HASHING_ALGORITHM}") String passwordHashingAlgorithm) {
        APPLICATION_NAME = applicationName;
        FRONTEND_ORIGIN = frontendOrigin;
        FULL_PASSWORD_LENGTH = fullPasswordLength;
        JWT_KEY = jwtKey;
        MASKED_PASSWORD_MAX_LENGTH = maskedPasswordMaxLength;
        MASKED_PASSWORD_MIN_LENGTH = maskedPasswordMinLength;
        PASSWORD_HASHING_ALGORITHM = passwordHashingAlgorithm;
    }
}
