package pl.lodz.p.it.securental.controllers.accounts.impl;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.it.securental.annotations.NeverTransaction;
import pl.lodz.p.it.securental.controllers.accounts.AccountController;
import pl.lodz.p.it.securental.dto.accounts.AccountDto;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.services.accounts.AccountService;

@CrossOrigin
@RestController
@AllArgsConstructor
@NeverTransaction
public class AccountControllerImpl implements AccountController {

    private final AccountService accountService;

    @Override
    @PostMapping("/account")
    public void addAccount(@RequestBody AccountDto accountDto) throws ApplicationBaseException {
        accountService.addAccount(accountDto);
    }

    @Override
    @PostMapping("/register")
    public String register(@RequestBody AccountDto accountDto) throws ApplicationBaseException {
        return accountService.register(accountDto);
    }

    @Override
    @GetMapping("/account/{username}")
    public AccountDto getAccount(@PathVariable String username) throws ApplicationBaseException {
        return accountService.getAccount(username);
    }
}
