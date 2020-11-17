package pl.lodz.p.it.securental.controllers.mok;

import org.springframework.data.domain.Page;
import pl.lodz.p.it.securental.dto.mok.AccountDto;
import pl.lodz.p.it.securental.dto.mok.ConfirmAccountDto;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public interface AccountController {

    void addAccount(AccountDto accountDto, String language) throws ApplicationBaseException;
    String register(AccountDto accountDto, String language) throws ApplicationBaseException;
    void confirmAccount(ConfirmAccountDto confirmAccountDto) throws ApplicationBaseException;
    AccountDto getAccount(String username) throws ApplicationBaseException;
    Page<AccountDto> getAllAccounts(int page, int size) throws ApplicationBaseException;
    Page<AccountDto> getSortedAccounts(int page, int size, String property, String order) throws ApplicationBaseException;
    Page<AccountDto> filterAccounts(String filter, int page, int size) throws ApplicationBaseException;
    Page<AccountDto> filterSortedAccounts(String filter, int page, int size, String property, String order) throws ApplicationBaseException;
}
