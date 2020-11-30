package pl.lodz.p.it.securental.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component(ApplicationProperties.APPLICATION_PROPERTIES_BEAN)
public class ApplicationProperties {

    public static final String APPLICATION_PROPERTIES_BEAN = "applicationProperties";
    public static final String ACCESS_LEVEL_ADMIN = "ADMIN";
    public static final String ACCESS_LEVEL_EMPLOYEE = "EMPLOYEE";
    public static final String ACCESS_LEVEL_CLIENT = "CLIENT";

    public static final String STRING_REGEX = "^[a-zA-Z0-9!@#$%^ąćęłńóśźżĄĆĘŁŃÓŚŹŻ]+$";
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9-_]+(\\.[a-zA-Z0-9-_]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9]+)*(\\.[a-zA-Z]{2,})$";

    public static String APPLICATION_NAME;
    public static Integer AUTHENTICATION_TOKEN_EXPIRATION;
    public static String FRONTEND_ORIGIN;
    public static Integer FULL_PASSWORD_LENGTH;
    public static Integer JWT_EXPIRATION_TIME;
    public static String JWT_KEY;
    public static char[] KEYSTORE_PASSWORD;
    public static Integer MASKED_PASSWORD_MAX_LENGTH;
    public static Integer MASKED_PASSWORD_MIN_LENGTH;
    public static String PASSWORD_HASHING_ALGORITHM;

    public ApplicationProperties(@Value("${APPLICATION_NAME}") String applicationName,
                                 @Value("${AUTHENTICATION_TOKEN_EXPIRATION}") Integer authenticationTokenExpiration,
                                 @Value("${FRONTEND_ORIGIN}") String frontendOrigin,
                                 @Value("${FULL_PASSWORD_LENGTH}") Integer fullPasswordLength,
                                 @Value("${JWT_KEY}") String jwtKey,
                                 @Value("${JWT_EXPIRATION_TIME}") Integer jwtExpirationTime,
                                 @Value("${KEYSTORE_PASSWORD}") String keystorePassword,
                                 @Value("${MASKED_PASSWORD_MAX_LENGTH}") Integer maskedPasswordMaxLength,
                                 @Value("${MASKED_PASSWORD_MIN_LENGTH}") Integer maskedPasswordMinLength,
                                 @Value("${PASSWORD_HASHING_ALGORITHM}") String passwordHashingAlgorithm) {
        APPLICATION_NAME = applicationName;
        AUTHENTICATION_TOKEN_EXPIRATION = authenticationTokenExpiration;
        FRONTEND_ORIGIN = frontendOrigin;
        FULL_PASSWORD_LENGTH = fullPasswordLength;
        JWT_EXPIRATION_TIME = jwtExpirationTime;
        JWT_KEY = jwtKey;
        KEYSTORE_PASSWORD = keystorePassword.toCharArray();
        MASKED_PASSWORD_MAX_LENGTH = maskedPasswordMaxLength;
        MASKED_PASSWORD_MIN_LENGTH = maskedPasswordMinLength;
        PASSWORD_HASHING_ALGORITHM = passwordHashingAlgorithm;
    }
}
