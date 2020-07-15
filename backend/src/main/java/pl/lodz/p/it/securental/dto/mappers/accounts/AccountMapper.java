package pl.lodz.p.it.securental.dto.mappers.accounts;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import pl.lodz.p.it.securental.dto.accounts.AccountDto;
import pl.lodz.p.it.securental.entities.accounts.Account;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountDto toAccountDto(Account account);
    Account toAccount(AccountDto accountDto);
    void updateAccountFromDto(AccountDto accountDto, @MappingTarget Account account);
}
