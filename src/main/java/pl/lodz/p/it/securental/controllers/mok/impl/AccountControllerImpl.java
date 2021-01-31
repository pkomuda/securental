package pl.lodz.p.it.securental.controllers.mok.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.it.securental.aop.annotations.CaptchaRequired;
import pl.lodz.p.it.securental.aop.annotations.NeverTransaction;
import pl.lodz.p.it.securental.aop.annotations.OtpAuthorizationRequired;
import pl.lodz.p.it.securental.controllers.mok.AccountController;
import pl.lodz.p.it.securental.dto.model.mok.AccountDto;
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
        return accountService.regenerateOwnQrCode(username);
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

    private String resolvePropertyName(String property) {
        if (property.equals("username")) {
            return "otpCredentials_username";
        } else {
            return property;
        }
    }
}
