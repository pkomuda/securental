package pl.lodz.p.it.securental.controllers.accounts;

import pl.lodz.p.it.securental.dto.accounts.AccountDto;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public interface AccountController {

    void addAccount(AccountDto accountDto) throws ApplicationBaseException;
    AccountDto getAccount(String username) throws ApplicationBaseException;
}
