package pl.lodz.p.it.securental.controllers.mok.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.it.securental.aop.annotations.CaptchaRequired;
import pl.lodz.p.it.securental.aop.annotations.NeverTransaction;
import pl.lodz.p.it.securental.aop.annotations.OtpAuthorizationRequired;
import pl.lodz.p.it.securental.controllers.mok.AccountController;
import pl.lodz.p.it.securental.dto.model.log.LogDto;
import pl.lodz.p.it.securental.dto.model.mok.AccountDto;
import pl.lodz.p.it.securental.dto.model.mok.ChangePasswordRequest;
import pl.lodz.p.it.securental.dto.model.mok.ConfirmAccountRequest;
import pl.lodz.p.it.securental.dto.model.mok.RegistrationResponse;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.services.mok.AccountService;
import pl.lodz.p.it.securental.utils.PagingHelper;

@RestController
@AllArgsConstructor
@NeverTransaction
public class AccountControllerImpl implements AccountController {

    private final AccountService accountService;

    @Override
    @PostMapping("/account")
    @PreAuthorize("hasAuthority('addAccount')")
    public RegistrationResponse addAccount(@RequestBody AccountDto accountDto,
                                           @RequestHeader("Accept-Language") String language) throws ApplicationBaseException {
        return accountService.addAccount(accountDto, language);
    }

    @Override
    @CaptchaRequired
    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    public RegistrationResponse register(@RequestBody AccountDto accountDto,
                                         @RequestHeader("Accept-Language") String language) throws ApplicationBaseException {
        return accountService.register(accountDto, language);
    }

    @Override
    @GetMapping("/qrCode/{username}")
    @PreAuthorize("hasAuthority('regenerateOwnQrCode') and #username == authentication.principal.username")
    public RegistrationResponse regenerateOwnQrCode(@PathVariable String username) throws ApplicationBaseException {
        return accountService.regenerateQrCode(username);
    }

    @Override
    @CaptchaRequired
    @PutMapping("/confirmAccount")
    @PreAuthorize("permitAll()")
    public void confirmAccount(@RequestBody ConfirmAccountRequest confirmAccountRequest) throws ApplicationBaseException {
        accountService.confirmAccount(confirmAccountRequest.getToken());
    }

    @Override
    @GetMapping("/account/{username}")
    @PreAuthorize("hasAuthority('getAccount')")
    public AccountDto getAccount(@PathVariable String username) throws ApplicationBaseException {
        return accountService.getAccount(username);
    }

    @Override
    @GetMapping("/ownAccount/{username}")
    @PreAuthorize("hasAuthority('getOwnAccount') and #username == authentication.principal.username")
    public AccountDto getOwnAccount(@PathVariable String username) throws ApplicationBaseException {
        return accountService.getAccount(username);
    }

    @Override
    @OtpAuthorizationRequired
    @PutMapping("/account/{username}")
    @PreAuthorize("hasAuthority('editAccount')")
    public void editAccount(@PathVariable String username,
                            @RequestBody AccountDto accountDto) throws ApplicationBaseException {
        accountService.editAccount(username, accountDto);
    }

    @Override
    @OtpAuthorizationRequired
    @PutMapping("/ownAccount/{username}")
    @PreAuthorize("hasAuthority('editOwnAccount') and #username == authentication.principal.username")
    public void editOwnAccount(@PathVariable String username,
                               @RequestBody AccountDto accountDto) throws ApplicationBaseException {
        accountService.editOwnAccount(username, accountDto);
    }

    @Override
    @GetMapping("/accounts/{page}/{size}")
    @PreAuthorize("hasAuthority('getAllAccounts')")
    public Page<AccountDto> getAllAccounts(@PathVariable int page,
                                           @PathVariable int size) throws ApplicationBaseException {
        return accountService.getAllAccounts(new PagingHelper(page, size));
    }

    @Override
    @GetMapping("/accounts/{page}/{size}/{property}/{order}")
    @PreAuthorize("hasAuthority('getSortedAccounts')")
    public Page<AccountDto> getSortedAccounts(@PathVariable int page,
                                              @PathVariable int size,
                                              @PathVariable String property,
                                              @PathVariable String order) throws ApplicationBaseException {
        return accountService.getAllAccounts(new PagingHelper(page, size, resolvePropertyName(property), order));
    }

    @Override
    @GetMapping("/accounts/{filter}/{page}/{size}")
    @PreAuthorize("hasAuthority('filterAccounts')")
    public Page<AccountDto> filterAccounts(@PathVariable String filter,
                                           @PathVariable int page,
                                           @PathVariable int size) throws ApplicationBaseException {
        return accountService.filterAccounts(filter, new PagingHelper(page, size));
    }

    @Override
    @GetMapping("/accounts/{filter}/{page}/{size}/{property}/{order}")
    @PreAuthorize("hasAuthority('filterSortedAccounts')")
    public Page<AccountDto> filterSortedAccounts(@PathVariable String filter,
                                                 @PathVariable int page,
                                                 @PathVariable int size,
                                                 @PathVariable String property,
                                                 @PathVariable String order) throws ApplicationBaseException {
        return accountService.filterAccounts(filter, new PagingHelper(page, size, resolvePropertyName(property), order));
    }

    @Override
    @PutMapping("/initializeResetPassword/{username}")
    @PreAuthorize("permitAll()")
    public void initializeResetPassword(@PathVariable String username) throws ApplicationBaseException {
        accountService.initializeResetPassword(username);
    }

    @Override
    @OtpAuthorizationRequired
    @PutMapping("/changePassword/{username}")
    @PreAuthorize("hasAuthority('changePassword')")
    public RegistrationResponse changePassword(@PathVariable String username,
                                               @RequestBody ChangePasswordRequest changePasswordRequest) throws ApplicationBaseException {
        return accountService.changePassword(username, changePasswordRequest);
    }

    @Override
    @OtpAuthorizationRequired
    @PutMapping("/changeOwnPassword/{username}")
    @PreAuthorize("hasAuthority('changeOwnPassword') and #username == authentication.principal.username")
    public RegistrationResponse changeOwnPassword(@PathVariable String username,
                                                  @RequestBody ChangePasswordRequest changePasswordRequest) throws ApplicationBaseException {
        return accountService.changeOwnPassword(username, changePasswordRequest);
    }

    @Override
    @CaptchaRequired
    @PutMapping("/resetOwnPassword/{hash}")
    @PreAuthorize("permitAll()")
    public RegistrationResponse resetOwnPassword(@PathVariable String hash,
                                                 @RequestBody ChangePasswordRequest changePasswordRequest) throws ApplicationBaseException {
        return accountService.resetOwnPassword(hash, changePasswordRequest);
    }

    @Override
    @OtpAuthorizationRequired
    @GetMapping("/resendConfirmationEmail/{username}")
    @PreAuthorize("hasAuthority('resendConfirmationEmail')")
    public void resendConfirmationEmail(@PathVariable String username) throws ApplicationBaseException {
        accountService.sendConfirmationEmail(username);
    }

    @Override
    @OtpAuthorizationRequired
    @GetMapping("/resendQrCodeEmail/{username}")
    @PreAuthorize("hasAuthority('resendQrCodeEmail')")
    public void resendQrCodeEmail(@PathVariable String username) throws ApplicationBaseException {
        accountService.sendQrCodeEmail(username);
    }

    @Override
    @PutMapping("/language/{username}/{language}")
    @PreAuthorize("hasAuthority('changePreferredLanguage')")
    public void changePreferredLanguage(@PathVariable String username,
                                        @PathVariable String language) throws ApplicationBaseException {
        accountService.changePreferredLanguage(username, language);
    }

    @Override
    @PutMapping("/theme/{username}/{theme}")
    @PreAuthorize("hasAuthority('changePreferredColorTheme')")
    public void changePreferredColorTheme(@PathVariable String username,
                                          @PathVariable String theme) throws ApplicationBaseException {
        accountService.changePreferredColorTheme(username, theme);
    }

    @Override
    @GetMapping("/logs/{page}/{size}")
    @PreAuthorize("hasAuthority('getAllLogs')")
    public Page<LogDto> getAllLogs(@PathVariable int page,
                                   @PathVariable int size) throws ApplicationBaseException {
        return accountService.getAllLogs(new PagingHelper(page, size));
    }

    @Override
    @GetMapping("/logs/{filter}/{page}/{size}")
    @PreAuthorize("hasAuthority('filterLogs')")
    public Page<LogDto> filterLogs(@PathVariable String filter,
                                   @PathVariable int page,
                                   @PathVariable int size) throws ApplicationBaseException {
        return accountService.filterLogs(filter, new PagingHelper(page, size));
    }

    private String resolvePropertyName(String property) {
        if (property.equals("username")) {
            return "otpCredentials_username";
        } else {
            return property;
        }
    }
}
