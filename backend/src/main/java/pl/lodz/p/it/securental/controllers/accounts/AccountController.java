package pl.lodz.p.it.securental.controllers.accounts;

import org.springframework.web.bind.annotation.RequestBody;
import pl.lodz.p.it.securental.dto.accounts.AccountDto;
import pl.lodz.p.it.securental.dto.accounts.ConfirmAccountDto;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public interface AccountController {

    void addAccount(AccountDto accountDto) throws ApplicationBaseException;
    String register(AccountDto accountDto, String language) throws ApplicationBaseException;
    void confirmAccount(ConfirmAccountDto confirmAccountDto) throws ApplicationBaseException;
    AccountDto getAccount(String username) throws ApplicationBaseException;
}
