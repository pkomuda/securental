package pl.lodz.p.it.securental.services.mok;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.adapters.BlacklistedJwtAdapter;
import pl.lodz.p.it.securental.adapters.mok.AccountAdapter;
import pl.lodz.p.it.securental.aop.annotations.RequiresNewTransaction;
import pl.lodz.p.it.securental.dto.model.mok.AuthenticationResponse;
import pl.lodz.p.it.securental.entities.BlacklistedJwt;
import pl.lodz.p.it.securental.entities.mok.AccessLevel;
import pl.lodz.p.it.securental.entities.mok.Account;
import pl.lodz.p.it.securental.entities.mok.AuthenticationToken;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.securental.utils.ApplicationProperties;
import pl.lodz.p.it.securental.utils.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@RequiresNewTransaction
public class AuthenticationService {

    private final AccountAdapter accountAdapter;
    private final BlacklistedJwtAdapter blacklistedJwtAdapter;

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

    public AuthenticationResponse.AuthenticationResponseBuilder finalizeLogin(String username, boolean successful) throws ApplicationBaseException {
        Optional<Account> accountOptional = accountAdapter.getAccount(username);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            if (successful) {
                account.setLastSuccessfulAuthentication(LocalDateTime.now());
                account.setFailedAuthenticationCounter(0);
                account.setLoginInitializationCounter(0);
            } else {
                account.setLastFailedAuthentication(LocalDateTime.now());
                account.setFailedAuthenticationCounter(account.getFailedAuthenticationCounter() + 1);
                if (account.getFailedAuthenticationCounter() >= ApplicationProperties.FAILED_AUTHENTICATION_MAX_COUNTER) {
                    account.setActive(false);
                }
            }
            return AuthenticationResponse.builder()
                    .lastSuccessfulAuthentication(StringUtils.localDateTimeToString(account.getLastSuccessfulAuthentication()))
                    .lastFailedAuthentication(StringUtils.localDateTimeToString(account.getLastFailedAuthentication()));
        } else {
            throw new AccountNotFoundException();
        }
    }

    public AuthenticationResponse.AuthenticationResponseBuilder currentUser(String username) throws ApplicationBaseException {
        Optional<Account> accountOptional = accountAdapter.getAccount(username);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            List<String> accessLevels = getUserFrontendRoles(account);
            return AuthenticationResponse.builder()
                    .username(username)
                    .accessLevels(accessLevels)
                    .currentAccessLevel(getHighestFrontendRole(accessLevels));
        } else {
            throw new AccountNotFoundException();
        }
    }

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
