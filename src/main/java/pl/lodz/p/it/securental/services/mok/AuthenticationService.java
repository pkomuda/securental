package pl.lodz.p.it.securental.services.mok;

import lombok.AllArgsConstructor;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.adapters.BlacklistedJwtAdapter;
import pl.lodz.p.it.securental.adapters.mok.AccountAdapter;
import pl.lodz.p.it.securental.aop.annotations.RequiresNewTransaction;
import pl.lodz.p.it.securental.dto.model.mok.AuthenticationResponse;
import pl.lodz.p.it.securental.entities.mok.AccessLevel;
import pl.lodz.p.it.securental.entities.mok.Account;
import pl.lodz.p.it.securental.entities.mok.AuthenticationToken;
import pl.lodz.p.it.securental.entities.mok.BlacklistedJwt;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.db.DatabaseConnectionException;
import pl.lodz.p.it.securental.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.securental.utils.ApplicationProperties;
import pl.lodz.p.it.securental.utils.EmailSender;
import pl.lodz.p.it.securental.utils.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@RequiresNewTransaction
@Retryable(DatabaseConnectionException.class)
public class AuthenticationService {

    private final AccountAdapter accountAdapter;
    private final BlacklistedJwtAdapter blacklistedJwtAdapter;
    private final EmailSender emailSender;

    //@PreAuthorize("permitAll()")
    public List<Integer> initializeLogin(String username) throws ApplicationBaseException {
        List<Integer> randomCombination = StringUtils.randomCombination(
                ApplicationProperties.FULL_PASSWORD_LENGTH,
                ApplicationProperties.MASKED_PASSWORD_MIN_LENGTH,
                ApplicationProperties.MASKED_PASSWORD_MAX_LENGTH);
        Optional<Account> accountOptional = accountAdapter.getAccount(username);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            account.setLoginInitializationCounter(account.getLoginInitializationCounter() + 1);
            AuthenticationToken authenticationToken = account.getAuthenticationToken();
            authenticationToken.setCombination(randomCombination);
            authenticationToken.setExpiration(LocalDateTime.now().plusMinutes(ApplicationProperties.AUTHENTICATION_TOKEN_EXPIRATION));
        } else {
            accountAdapter.getAccount(ApplicationProperties.ADMIN_PRINCIPAL);
        }
        return randomCombination;
    }

    //@PreAuthorize("permitAll()")
    public AuthenticationResponse.AuthenticationResponseBuilder finalizeLogin(String username, String ipAddress, boolean successful) throws ApplicationBaseException {
        Optional<Account> accountOptional = accountAdapter.getAccount(username);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
            String previousSuccessfulAuthentication = StringUtils.localDateTimeToString(account.getLastSuccessfulAuthentication());
            String previousFailedAuthentication = StringUtils.localDateTimeToString(account.getLastFailedAuthentication());
            String previousAuthenticationIpAddress = account.getLastAuthenticationIpAddress();
            if (successful) {
                account.setLastSuccessfulAuthentication(now);
                account.setFailedAuthenticationCounter(0);
                account.setLoginInitializationCounter(0);
                account.setLastAuthenticationIpAddress(ipAddress);
                if (getUserFrontendRoles(account).contains(ApplicationProperties.ACCESS_LEVEL_ADMIN)) {
                    String language = account.getPreferredLanguage();
                    String subject = StringUtils.getTranslatedText("admin.subject", language);
                    String text = StringUtils.getTranslatedText("admin.date", language) + ": " + now.format(formatter) + "<br/><br/>"
                            + StringUtils.getTranslatedText("admin.ipAddress", language) + ": " + ipAddress;
                    emailSender.sendMessage(account.getEmail(), subject, text);
                }
            } else {
                account.setLastFailedAuthentication(now);
                account.setFailedAuthenticationCounter(account.getFailedAuthenticationCounter() + 1);
                if (account.getFailedAuthenticationCounter() >= ApplicationProperties.FAILED_AUTHENTICATION_MAX_COUNTER) {
                    account.setActive(false);
                }
            }
            return AuthenticationResponse.builder()
                    .preferredLanguage(account.getPreferredLanguage())
                    .lastSuccessfulAuthentication(previousSuccessfulAuthentication)
                    .lastFailedAuthentication(previousFailedAuthentication)
                    .lastAuthenticationIpAddress(previousAuthenticationIpAddress);
        } else {
            throw new AccountNotFoundException();
        }
    }

    //@PreAuthorize("hasAuthority('currentUser')")
    public AuthenticationResponse.AuthenticationResponseBuilder currentUser(String username) throws ApplicationBaseException {
        Optional<Account> accountOptional = accountAdapter.getAccount(username);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            List<String> accessLevels = getUserFrontendRoles(account);
            return AuthenticationResponse.builder()
                    .username(username)
                    .accessLevels(accessLevels)
                    .currentAccessLevel(getHighestFrontendRole(accessLevels))
                    .preferredLanguage(account.getPreferredLanguage());
        } else {
            throw new AccountNotFoundException();
        }
    }

    //@PreAuthorize("hasAuthority('logout')")
    public void addJwtToBlacklist(String jwt, long expiration) throws ApplicationBaseException {
        blacklistedJwtAdapter.addBlacklistedJwt(
                BlacklistedJwt.builder()
                        .token(jwt)
                        .expiration(LocalDateTime.ofInstant(Instant.ofEpochMilli(expiration), ZoneId.systemDefault()))
                        .build()
        );
    }

    private List<String> getUserFrontendRoles(Account account) {
        return account.getAccessLevels().stream()
                .filter(AccessLevel::getActive)
                .map(AccessLevel::getName)
                .sorted(Comparator.comparing(ApplicationProperties.ACCESS_LEVEL_ORDER::indexOf))
                .collect(Collectors.toList());
    }

    private String getHighestFrontendRole(List<String> roles) {
        if (roles.contains(ApplicationProperties.ACCESS_LEVEL_ADMIN)) {
            return ApplicationProperties.ACCESS_LEVEL_ADMIN;
        } else if (roles.contains(ApplicationProperties.ACCESS_LEVEL_EMPLOYEE)) {
            return ApplicationProperties.ACCESS_LEVEL_EMPLOYEE;
        } else {
            return ApplicationProperties.ACCESS_LEVEL_CLIENT;
        }
    }
}
