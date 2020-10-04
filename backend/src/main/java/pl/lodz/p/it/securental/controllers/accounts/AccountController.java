package pl.lodz.p.it.securental.controllers.accounts;

import pl.lodz.p.it.securental.dto.accounts.AccountDto;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

import javax.servlet.http.HttpServletResponse;

public interface AccountController {

    void addAccount(AccountDto accountDto) throws ApplicationBaseException;
    void register(AccountDto accountDto, HttpServletResponse response) throws Exception;
    AccountDto getAccount(String username) throws ApplicationBaseException;
}
