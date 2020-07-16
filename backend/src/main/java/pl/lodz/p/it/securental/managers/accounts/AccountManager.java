package pl.lodz.p.it.securental.managers.accounts;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.it.securental.dto.accounts.AccountDto;
import pl.lodz.p.it.securental.dto.mappers.accounts.AccountMapper;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.services.accounts.AccountService;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = ApplicationBaseException.class, propagation = Propagation.REQUIRES_NEW)
public class AccountManager {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    public void addAccount(AccountDto accountDto) throws ApplicationBaseException {
        accountService.addAccount(accountMapper.toAccount(accountDto));
    }

    public AccountDto getAccount(String username) throws ApplicationBaseException {
        return accountMapper.toAccountDto(accountService.getAccount(username));
    }
}
