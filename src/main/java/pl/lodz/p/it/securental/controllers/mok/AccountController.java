package pl.lodz.p.it.securental.controllers.mok;

import org.springframework.data.domain.Page;
import pl.lodz.p.it.securental.dto.mok.AccountDto;
import pl.lodz.p.it.securental.dto.mok.ConfirmAccountRequest;
import pl.lodz.p.it.securental.dto.mok.RegistrationResponse;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public interface AccountController {

    RegistrationResponse addAccount(AccountDto accountDto, String language) throws ApplicationBaseException;
    RegistrationResponse register(AccountDto accountDto, String language) throws ApplicationBaseException;
    void confirmAccount(ConfirmAccountRequest confirmAccountRequest) throws ApplicationBaseException;
    AccountDto getAccount(String username) throws ApplicationBaseException;
    AccountDto getOwnAccount(String username) throws ApplicationBaseException;
    void editAccount(String username, AccountDto accountDto) throws ApplicationBaseException;
    void editOwnAccount(String username, AccountDto accountDto) throws ApplicationBaseException;
    Page<AccountDto> getAllAccounts(int page, int size) throws ApplicationBaseException;
    Page<AccountDto> getSortedAccounts(int page, int size, String property, String order) throws ApplicationBaseException;
    Page<AccountDto> filterAccounts(String filter, int page, int size) throws ApplicationBaseException;
    Page<AccountDto> filterSortedAccounts(String filter, int page, int size, String property, String order) throws ApplicationBaseException;
}
