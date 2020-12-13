package pl.lodz.p.it.securental.controllers.mok.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.it.securental.annotations.NeverTransaction;
import pl.lodz.p.it.securental.controllers.mok.AccountController;
import pl.lodz.p.it.securental.dto.mok.AccountDto;
import pl.lodz.p.it.securental.dto.mok.ConfirmAccountRequest;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.services.mok.AccountService;
import pl.lodz.p.it.securental.utils.PagingHelper;
import pl.lodz.p.it.securental.utils.SignatureUtils;

@RestController
@AllArgsConstructor
@NeverTransaction
public class AccountControllerImpl implements AccountController {

    private final AccountService accountService;
    private final SignatureUtils signatureUtils;

    @Override
    @PostMapping("/account")
    public void addAccount(@RequestBody AccountDto accountDto,
                           @RequestHeader("Accept-Language") String language) throws ApplicationBaseException {
        accountService.addAccount(accountDto, language);
    }

    @Override
    @PostMapping("/register")
    public String register(@RequestBody AccountDto accountDto,
                           @RequestHeader("Accept-Language") String language) throws ApplicationBaseException {
        return accountService.register(accountDto, language);
    }

    @Override
    @PutMapping("/confirm")
    public void confirmAccount(@RequestBody ConfirmAccountRequest confirmAccountRequest) throws ApplicationBaseException {
        accountService.confirmAccount(confirmAccountRequest.getToken());
    }

    @Override
    @GetMapping("/account/{username}")
    public AccountDto getAccount(@PathVariable String username) throws ApplicationBaseException {
        return accountService.getAccount(username);
    }

    @Override
    @GetMapping("/accounts/{page}/{size}")
    public Page<AccountDto> getAllAccounts(@PathVariable int page,
                                           @PathVariable int size) throws ApplicationBaseException {
        return accountService.getAllAccounts(new PagingHelper(page, size));
    }

    @Override
    @GetMapping("/accounts/{page}/{size}/{property}/{order}")
    public Page<AccountDto> getSortedAccounts(@PathVariable int page,
                                              @PathVariable int size,
                                              @PathVariable String property,
                                              @PathVariable String order) throws ApplicationBaseException {
        return accountService.getAllAccounts(new PagingHelper(page, size, resolvePropertyName(property), order));
    }

    @Override
    @GetMapping("/accounts/{filter}/{page}/{size}")
    public Page<AccountDto> filterAccounts(@PathVariable String filter,
                                           @PathVariable int page,
                                           @PathVariable int size) throws ApplicationBaseException {
        return accountService.filterAccounts(filter, new PagingHelper(page, size));
    }

    @Override
    @GetMapping("/accounts/{filter}/{page}/{size}/{property}/{order}")
    public Page<AccountDto> filterSortedAccounts(@PathVariable String filter,
                                                 @PathVariable int page,
                                                 @PathVariable int size,
                                                 @PathVariable String property,
                                                 @PathVariable String order) throws ApplicationBaseException {
        return accountService.filterAccounts(filter, new PagingHelper(page, size, resolvePropertyName(property), order));
    }

    @Override
    @PutMapping("/account/{username}")
    public void editAccount(@PathVariable String username,
                            @RequestBody AccountDto accountDto) throws ApplicationBaseException {
        accountService.editAccount(username, accountDto);
    }

    @GetMapping("/sign/{message}")
    public String sign(@PathVariable String message) throws ApplicationBaseException {
        return signatureUtils.sign(message);
    }

    @GetMapping("/verify/{message}/{received}")
    public boolean verify(@PathVariable String message, @PathVariable String received) throws ApplicationBaseException {
        return signatureUtils.verify(message, received);
    }

    private String resolvePropertyName(String property) {
        if (property.equals("username")) {
            return "otpCredentials_username";
        } else {
            return property;
        }
    }
}