package pl.lodz.p.it.securental.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(ApplicationProperties.APPLICATION_PROPERTIES_BEAN)
public class ApplicationProperties {

    public static final String APPLICATION_PROPERTIES_BEAN = "applicationProperties";

    public static final String ACCESS_LEVEL_ADMIN = "ADMIN";
    public static final String ACCESS_LEVEL_EMPLOYEE = "EMPLOYEE";
    public static final String ACCESS_LEVEL_CLIENT = "CLIENT";
    public static final List<String> ACCESS_LEVEL_ORDER = List.of(ACCESS_LEVEL_ADMIN, ACCESS_LEVEL_EMPLOYEE, ACCESS_LEVEL_CLIENT);

    public static final String RESERVATION_STATUS_NEW = "NEW";
    public static final String RESERVATION_STATUS_CANCELLED = "CANCELLED";
    public static final String RESERVATION_STATUS_FINISHED = "FINISHED";

    public static final String STRING_REGEX = "^[a-zA-Z0-9!@#$%^ąćęłńóśźżĄĆĘŁŃÓŚŹŻ]+$";
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9-_]+(\\.[a-zA-Z0-9-_]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9]+)*(\\.[a-zA-Z]{2,})$";

    public static final String[] PUBLIC_ROUTES = {"/api/register", "/api/confirmAccount", "/api/initializeLogin/*", "/api/login", "/api/car/*", "/api/cars/**"};

    public static Integer ADMIN_OTP_CODE;
    public static String ADMIN_PRINCIPAL;
    public static String[] ADMIN_ROLES;
    public static Integer AUTHENTICATION_TOKEN_EXPIRATION;
    public static String[] CLIENT_ROLES;
    public static String[] EMPLOYEE_ROLES;
    public static String FRONTEND_ORIGIN;
    public static Integer FULL_PASSWORD_LENGTH;
    public static Integer JWT_EXPIRATION_TIME;
    public static String JWT_KEY;
    public static char[] KEYSTORE_PASSWORD;
    public static String LAST_PASSWORD_CHARACTERS;
    public static Integer LAST_PASSWORD_CHARACTERS_LENGTH;
    public static Integer MASKED_PASSWORD_MAX_LENGTH;
    public static Integer MASKED_PASSWORD_MIN_LENGTH;
    public static String PASSWORD_HASHING_ALGORITHM;
    public static String UNAUTHENTICATED_PRINCIPAL;

    public ApplicationProperties(@Value("${admin.otp.code}") Integer adminOtpCode,
                                 @Value("${admin.principal}") String adminPrincipal,
                                 @Value("${admin.roles}") String adminRoles,
                                 @Value("${authentication.token.expiration}") Integer authenticationTokenExpiration,
                                 @Value("${client.roles}") String clientRoles,
                                 @Value("${employee.roles}") String employeeRoles,
                                 @Value("${frontend.origin}") String frontendOrigin,
                                 @Value("${full.password.length}") Integer fullPasswordLength,
                                 @Value("${jwt.expiration.time}") Integer jwtExpirationTime,
                                 @Value("${jwt.key}") String jwtKey,
                                 @Value("${keystore.password}") String keystorePassword,
                                 @Value("${last.password.characters}") String lastPasswordCharacters,
                                 @Value("${masked.password.max.length}") Integer maskedPasswordMaxLength,
                                 @Value("${masked.password.min.length}") Integer maskedPasswordMinLength,
                                 @Value("${password.hashing.algorithm}") String passwordHashingAlgorithm,
                                 @Value("${unauthenticated.principal}") String unauthenticatedPrincipal) {
        ADMIN_OTP_CODE = adminOtpCode;
        ADMIN_PRINCIPAL = adminPrincipal;
        ADMIN_ROLES = adminRoles.split(",");
        AUTHENTICATION_TOKEN_EXPIRATION = authenticationTokenExpiration;
        CLIENT_ROLES = clientRoles.split(",");
        EMPLOYEE_ROLES = employeeRoles.split(",");
        FRONTEND_ORIGIN = frontendOrigin;
        FULL_PASSWORD_LENGTH = fullPasswordLength;
        JWT_EXPIRATION_TIME = jwtExpirationTime;
        JWT_KEY = jwtKey;
        KEYSTORE_PASSWORD = keystorePassword.toCharArray();
        LAST_PASSWORD_CHARACTERS = lastPasswordCharacters;
        LAST_PASSWORD_CHARACTERS_LENGTH = fullPasswordLength - 8;
        MASKED_PASSWORD_MAX_LENGTH = maskedPasswordMaxLength;
        MASKED_PASSWORD_MIN_LENGTH = maskedPasswordMinLength;
        PASSWORD_HASHING_ALGORITHM = passwordHashingAlgorithm;
        UNAUTHENTICATED_PRINCIPAL = unauthenticatedPrincipal;
    }

    public static boolean isProduction() {
        return FRONTEND_ORIGIN.startsWith("https");
    }
}
