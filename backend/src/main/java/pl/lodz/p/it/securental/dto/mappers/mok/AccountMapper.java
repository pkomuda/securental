package pl.lodz.p.it.securental.dto.mappers.mok;

import pl.lodz.p.it.securental.dto.mok.AccountDto;
import pl.lodz.p.it.securental.entities.mok.Account;

public class AccountMapper {

    public static Account toAccount(AccountDto accountDto) {
        return Account.builder()
                .email(accountDto.getEmail())
                .fullPassword(accountDto.getPassword())
                .firstName(accountDto.getFirstName())
                .lastName(accountDto.getLastName())
                .active(accountDto.isActive())
                .confirmed(accountDto.isConfirmed())
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
                .build();
    }
}
