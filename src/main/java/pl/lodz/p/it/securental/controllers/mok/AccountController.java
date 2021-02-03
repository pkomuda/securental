package pl.lodz.p.it.securental.controllers.mok;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import pl.lodz.p.it.securental.dto.model.mok.AccountDto;
import pl.lodz.p.it.securental.dto.model.mok.ChangePasswordRequest;
import pl.lodz.p.it.securental.dto.model.mok.ConfirmAccountRequest;
import pl.lodz.p.it.securental.dto.model.mok.RegistrationResponse;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public interface AccountController {

    RegistrationResponse addAccount(AccountDto accountDto, String language) throws ApplicationBaseException;
    RegistrationResponse register(AccountDto accountDto, String language) throws ApplicationBaseException;
    RegistrationResponse regenerateOwnQrCode(String username) throws ApplicationBaseException;
    void confirmAccount(ConfirmAccountRequest confirmAccountRequest) throws ApplicationBaseException;
    AccountDto getAccount(String username) throws ApplicationBaseException;
    AccountDto getOwnAccount(String username) throws ApplicationBaseException;
    void editAccount(String username, AccountDto accountDto) throws ApplicationBaseException;
    void editOwnAccount(String username, AccountDto accountDto) throws ApplicationBaseException;
    Page<AccountDto> getAllAccounts(int page, int size) throws ApplicationBaseException;
    Page<AccountDto> getSortedAccounts(int page, int size, String property, String order) throws ApplicationBaseException;
    Page<AccountDto> filterAccounts(String filter, int page, int size) throws ApplicationBaseException;
    Page<AccountDto> filterSortedAccounts(String filter, int page, int size, String property, String order) throws ApplicationBaseException;
    void initializeResetPassword(String username, String language) throws ApplicationBaseException;
    RegistrationResponse changePassword(String username, ChangePasswordRequest changePasswordRequest) throws ApplicationBaseException;
    RegistrationResponse changeOwnPassword(String username, ChangePasswordRequest changePasswordRequest) throws ApplicationBaseException;
    RegistrationResponse resetOwnPassword(String hash, ChangePasswordRequest changePasswordRequest) throws ApplicationBaseException;
    void resendConfirmationEmail(String username) throws ApplicationBaseException;
    void resendQrCodeEmail(String username) throws ApplicationBaseException;
}
