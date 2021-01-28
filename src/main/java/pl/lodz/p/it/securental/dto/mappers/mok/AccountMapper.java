package pl.lodz.p.it.securental.dto.mappers.mok;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import pl.lodz.p.it.securental.dto.model.mok.AccountDto;
import pl.lodz.p.it.securental.entities.mok.*;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.utils.ApplicationProperties;
import pl.lodz.p.it.securental.utils.SignatureUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class AccountMapper {

    private final SignatureUtils signatureUtils;

    public static Account toAccount(AccountDto accountDto) {
        Account account = Account.builder()
                .email(accountDto.getEmail())
                .fullPassword(accountDto.getPassword())
                .firstName(accountDto.getFirstName())
                .lastName(accountDto.getLastName())
                .active(accountDto.isActive())
                .confirmed(accountDto.isConfirmed())
                .build();
        account.setAccessLevels(toAccessLevels(account, accountDto.getAccessLevels()));
        return account;
    }

    public AccountDto toAccountDtoWithSignature(Account account) throws ApplicationBaseException {
        return AccountDto.builder()
                .username(account.getOtpCredentials().getUsername())
                .email(account.getEmail())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .active(account.isActive())
                .confirmed(account.isConfirmed())
                .accessLevels(toAccessLevelStrings(account.getAccessLevels()))
                .signature(signatureUtils.sign(account.toSignString()))
                .build();
    }

    public static AccountDto toAccountDtoWithoutSignature(Account account) {
        return AccountDto.builder()
                .username(account.getOtpCredentials().getUsername())
                .email(account.getEmail())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .active(account.isActive())
                .confirmed(account.isConfirmed())
                .accessLevels(toAccessLevelStrings(account.getAccessLevels()))
                .build();
    }

    public static Page<AccountDto> toAccountDtos(Page<Account> accounts) {
        return accounts.map(AccountMapper::toAccountDtoWithoutSignature);
    }

    private static List<AccessLevel> toAccessLevels(Account account, List<String> accessLevelStrings) {
        List<AccessLevel> accessLevels = new ArrayList<>();
        if (accessLevelStrings.contains(ApplicationProperties.ACCESS_LEVEL_ADMIN)) {
            accessLevels.add(new Admin(ApplicationProperties.ACCESS_LEVEL_ADMIN, true, account));
        } else {
            accessLevels.add(new Admin(ApplicationProperties.ACCESS_LEVEL_ADMIN, false, account));
        }

        if (accessLevelStrings.contains(ApplicationProperties.ACCESS_LEVEL_EMPLOYEE)) {
            accessLevels.add(new Employee(ApplicationProperties.ACCESS_LEVEL_EMPLOYEE, true, account));
        } else {
            accessLevels.add(new Employee(ApplicationProperties.ACCESS_LEVEL_EMPLOYEE, false, account));
        }

        if (accessLevelStrings.contains(ApplicationProperties.ACCESS_LEVEL_CLIENT)) {
            accessLevels.add(new Client(ApplicationProperties.ACCESS_LEVEL_CLIENT, true, account));
        } else {
            accessLevels.add(new Client(ApplicationProperties.ACCESS_LEVEL_CLIENT, false, account));
        }
        return accessLevels;
    }

    public static void updateAccessLevels(List<AccessLevel> accessLevels, List<String> accessLevelStrings) {
        for (AccessLevel accessLevel : accessLevels) {
            if (accessLevel instanceof Admin) {
                accessLevel.setActive(accessLevelStrings.contains(ApplicationProperties.ACCESS_LEVEL_ADMIN));
            } else if (accessLevel instanceof Employee) {
                accessLevel.setActive(accessLevelStrings.contains(ApplicationProperties.ACCESS_LEVEL_EMPLOYEE));
            } else if (accessLevel instanceof Client) {
                accessLevel.setActive(accessLevelStrings.contains(ApplicationProperties.ACCESS_LEVEL_CLIENT));
            }
        }
    }

    private static List<String> toAccessLevelStrings(List<AccessLevel> accessLevels) {
        return accessLevels.stream()
                .filter(AccessLevel::isActive)
                .map(AccessLevel::getName)
                .collect(Collectors.toList());
    }
}
