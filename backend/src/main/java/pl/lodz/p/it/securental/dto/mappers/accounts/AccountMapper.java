package pl.lodz.p.it.securental.dto.mappers.accounts;

import pl.lodz.p.it.securental.dto.accounts.AccountDto;
import pl.lodz.p.it.securental.entities.accounts.Account;

public class AccountMapper {

    public static Account toAccount(AccountDto accountDto) {
        return Account.builder()
                .email(accountDto.getEmail())
                .firstName(accountDto.getFirstName())
                .lastName(accountDto.getLastName())
                .active(accountDto.isActive())
                .confirmed(accountDto.isConfirmed())
                .build();
    }

    public static AccountDto toAccountDto(Account account) {
        return AccountDto.builder()
                .username(account.getTotpCredentials().getUsername())
                .email(account.getEmail())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .active(account.isActive())
                .confirmed(account.isConfirmed())
                .build();
    }
}
