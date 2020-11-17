package pl.lodz.p.it.securental.dto.mappers.mok;

import org.springframework.data.domain.Page;
import pl.lodz.p.it.securental.dto.mok.AccountDto;
import pl.lodz.p.it.securental.entities.mok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static pl.lodz.p.it.securental.utils.ApplicationProperties.*;

public class AccountMapper {

    public static Account toAccount(AccountDto accountDto) {
        return Account.builder()
                .email(accountDto.getEmail())
                .fullPassword(accountDto.getPassword())
                .firstName(accountDto.getFirstName())
                .lastName(accountDto.getLastName())
                .active(accountDto.isActive())
                .confirmed(accountDto.isConfirmed())
                .accessLevels(toAccessLevels(accountDto.getAccessLevels()))
                .build();
    }

    public static AccountDto toAccountDto(Account account) {
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
        return accounts.map(AccountMapper::toAccountDto);
    }

    private static List<AccessLevel> toAccessLevels(List<String> accessLevelStrings) {
        List<AccessLevel> accessLevels = new ArrayList<>();
        if (accessLevelStrings.contains(ACCESS_LEVEL_ADMIN)) {
            accessLevels.add(new Admin(ACCESS_LEVEL_ADMIN, true));
        } else {
            accessLevels.add(new Admin(ACCESS_LEVEL_ADMIN, false));
        }

        if (accessLevelStrings.contains(ACCESS_LEVEL_EMPLOYEE)) {
            accessLevels.add(new Employee(ACCESS_LEVEL_EMPLOYEE, true));
        } else {
            accessLevels.add(new Employee(ACCESS_LEVEL_EMPLOYEE, false));
        }

        if (accessLevelStrings.contains(ACCESS_LEVEL_CLIENT)) {
            accessLevels.add(new Client(ACCESS_LEVEL_CLIENT, true));
        } else {
            accessLevels.add(new Client(ACCESS_LEVEL_CLIENT, false));
        }
        return accessLevels;
    }

    private static List<String> toAccessLevelStrings(List<AccessLevel> accessLevels) {
        return accessLevels.stream()
                .filter(AccessLevel::isActive)
                .map(AccessLevel::getName)
                .collect(Collectors.toList());
    }
}
