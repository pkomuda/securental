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

    public static final String IMAGE_FRONT = "front";
    public static final String IMAGE_RIGHT = "right";
    public static final String IMAGE_BACK = "back";
    public static final String IMAGE_LEFT = "left";

    public static final String DATETIME_REGEX = "^\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d:[0-5]\\d$";
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9-_]+(\\.[a-zA-Z0-9-_]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9]+)*(\\.[a-zA-Z]{2,})$";
    public static final String MONEY_REGEX = "^(?=.*[1-9])[0-9]*[.,]?[0-9]{1,2}$";
    public static final String NAME_REGEX = "^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+$";
    public static final String STATUS_REGEX = "^(" + RESERVATION_STATUS_NEW + "|" + RESERVATION_STATUS_CANCELLED + "|" + RESERVATION_STATUS_FINISHED + ")$";
    public static final String STRING_REGEX = "^[a-zA-Z0-9!@#$%ąćęłńóśźżĄĆĘŁŃÓŚŹŻ,. ]+$";

    public static final String LOG_CACHE_NAME = "log-cache";
    public static final String[] PUBLIC_ROUTES = {"/api/register", "/api/confirmAccount", "/api/initializeLogin/*", "/api/login", "/api/car/*", "/api/cars/**", "/api/initializeResetPassword/*", "/api/resetOwnPassword/*", "/api/s3"};

    public static Integer ADMIN_OTP_CODE;
    public static String ADMIN_PRINCIPAL;
    public static String[] ADMIN_ROLES;
    public static Integer AUTHENTICATION_TOKEN_EXPIRATION;
    public static String CAPTCHA_SECRET_KEY;
    public static String[] CLIENT_ROLES;
    public static String[] EMPLOYEE_ROLES;
    public static Integer FAILED_AUTHENTICATION_MAX_COUNTER;
    public static String FRONTEND_ORIGIN;
    public static Integer FULL_PASSWORD_LENGTH;
    public static Integer JWT_EXPIRATION_TIME;
    public static String JWT_KEY;
    public static char[] KEYSTORE_PASSWORD;
    public static String LAST_PASSWORD_CHARACTERS;
    public static Integer LAST_PASSWORD_CHARACTERS_LENGTH;
    public static Boolean LOG_CACHE_ENABLE;
    public static Integer LOGIN_INITIALIZATION_MAX_COUNTER;
    public static Integer MASKED_PASSWORD_MAX_LENGTH;
    public static Integer MASKED_PASSWORD_MIN_LENGTH;
    public static String PASSWORD_HASHING_ALGORITHM;
    public static Long RESET_PASSWORD_TOKEN_EXPIRATION;
    public static String UNAUTHENTICATED_PRINCIPAL;

    public ApplicationProperties(@Value("${admin.otp.code}") Integer adminOtpCode,
                                 @Value("${admin.principal}") String adminPrincipal,
                                 @Value("${admin.roles}") String adminRoles,
                                 @Value("${authentication.token.expiration}") Integer authenticationTokenExpiration,
                                 @Value("${captcha.secret.key}") String captchaSecretKey,
                                 @Value("${client.roles}") String clientRoles,
                                 @Value("${employee.roles}") String employeeRoles,
                                 @Value("${failed.authentication.max.counter}") Integer failedAuthenticationMaxCounter,
                                 @Value("${frontend.origin}") String frontendOrigin,
                                 @Value("${full.password.length}") Integer fullPasswordLength,
                                 @Value("${jwt.expiration.time}") Integer jwtExpirationTime,
                                 @Value("${jwt.key}") String jwtKey,
                                 @Value("${keystore.password}") String keystorePassword,
                                 @Value("${last.password.characters}") String lastPasswordCharacters,
                                 @Value("${log.cache.enable}") Boolean logCacheEnable,
                                 @Value("${login.initialization.max.counter}") Integer loginInitializationMaxCounter,
                                 @Value("${masked.password.max.length}") Integer maskedPasswordMaxLength,
                                 @Value("${masked.password.min.length}") Integer maskedPasswordMinLength,
                                 @Value("${password.hashing.algorithm}") String passwordHashingAlgorithm,
                                 @Value("${reset.password.token.expiration}") Long resetPasswordTokenExpiration,
                                 @Value("${unauthenticated.principal}") String unauthenticatedPrincipal) {
        ADMIN_OTP_CODE = adminOtpCode;
        ADMIN_PRINCIPAL = adminPrincipal;
        ADMIN_ROLES = adminRoles.split(",");
        AUTHENTICATION_TOKEN_EXPIRATION = authenticationTokenExpiration;
        CAPTCHA_SECRET_KEY = captchaSecretKey;
        CLIENT_ROLES = clientRoles.split(",");
        EMPLOYEE_ROLES = employeeRoles.split(",");
        FAILED_AUTHENTICATION_MAX_COUNTER = failedAuthenticationMaxCounter;
        FRONTEND_ORIGIN = frontendOrigin;
        FULL_PASSWORD_LENGTH = fullPasswordLength;
        JWT_EXPIRATION_TIME = jwtExpirationTime;
        JWT_KEY = jwtKey;
        KEYSTORE_PASSWORD = keystorePassword.toCharArray();
        LAST_PASSWORD_CHARACTERS = lastPasswordCharacters;
        LAST_PASSWORD_CHARACTERS_LENGTH = fullPasswordLength - 8;
        LOG_CACHE_ENABLE = logCacheEnable;
        LOGIN_INITIALIZATION_MAX_COUNTER = loginInitializationMaxCounter;
        MASKED_PASSWORD_MAX_LENGTH = maskedPasswordMaxLength;
        MASKED_PASSWORD_MIN_LENGTH = maskedPasswordMinLength;
        PASSWORD_HASHING_ALGORITHM = passwordHashingAlgorithm;
        RESET_PASSWORD_TOKEN_EXPIRATION = resetPasswordTokenExpiration;
        UNAUTHENTICATED_PRINCIPAL = unauthenticatedPrincipal;
    }

    public static boolean isProduction() {
        return FRONTEND_ORIGIN.startsWith("https");
    }
}
