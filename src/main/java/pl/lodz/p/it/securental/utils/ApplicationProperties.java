package pl.lodz.p.it.securental.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component(ApplicationProperties.APPLICATION_PROPERTIES_BEAN)
@PropertySource("file:src/main/resources/application.properties")
public class ApplicationProperties {

    public static final String APPLICATION_PROPERTIES_BEAN = "applicationProperties";
    public static final String ACCESS_LEVEL_ADMIN = "ADMIN";
    public static final String ACCESS_LEVEL_EMPLOYEE = "EMPLOYEE";
    public static final String ACCESS_LEVEL_CLIENT = "CLIENT";
    public static final String RESERVATION_STATUS_NEW = "NEW";
    public static final String RESERVATION_STATUS_CANCELLED = "CANCELLED";
    public static final String RESERVATION_STATUS_FINISHED = "FINISHED";

    public static final String STRING_REGEX = "^[a-zA-Z0-9!@#$%^ąćęłńóśźżĄĆĘŁŃÓŚŹŻ]+$";
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9-_]+(\\.[a-zA-Z0-9-_]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9]+)*(\\.[a-zA-Z]{2,})$";

    public static String[] ADMIN_ROLES;
    public static Integer AUTHENTICATION_TOKEN_EXPIRATION;
    public static String[] CLIENT_ROLES;
    public static String[] EMPLOYEE_ROLES;
    public static String FRONTEND_ORIGIN;
    public static Integer FULL_PASSWORD_LENGTH;
    public static Integer JWT_EXPIRATION_TIME;
    public static String JWT_KEY;
    public static char[] KEYSTORE_PASSWORD;
    public static Integer MASKED_PASSWORD_MAX_LENGTH;
    public static Integer MASKED_PASSWORD_MIN_LENGTH;
    public static String PASSWORD_HASHING_ALGORITHM;

    public ApplicationProperties(@Value("${admin.roles}") String adminRoles,
                                 @Value("${authentication.token.expiration}") Integer authenticationTokenExpiration,
                                 @Value("${client.roles}") String clientRoles,
                                 @Value("${employee.roles}") String employeeRoles,
                                 @Value("${frontend.origin}") String frontendOrigin,
                                 @Value("${full.password.length}") Integer fullPasswordLength,
                                 @Value("${jwt.expiration.time}") Integer jwtExpirationTime,
                                 @Value("${jwt.key}") String jwtKey,
                                 @Value("${keystore.password}") String keystorePassword,
                                 @Value("${masked.password.max.length}") Integer maskedPasswordMaxLength,
                                 @Value("${masked.password.min.length}") Integer maskedPasswordMinLength,
                                 @Value("${password.hashing.algorithm}") String passwordHashingAlgorithm) {
        ADMIN_ROLES = adminRoles.split(",");
        AUTHENTICATION_TOKEN_EXPIRATION = authenticationTokenExpiration;
        CLIENT_ROLES = clientRoles.split(",");
        EMPLOYEE_ROLES = employeeRoles.split(",");
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
