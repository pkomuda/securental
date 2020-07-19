package pl.lodz.p.it.securental.controllers.accounts.impl;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.it.securental.controllers.accounts.AccountController;
import pl.lodz.p.it.securental.dto.accounts.AccountDto;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.managers.accounts.AccountManager;

@RestController
@AllArgsConstructor
@Transactional(rollbackFor = ApplicationBaseException.class, propagation = Propagation.NEVER)
public class AccountControllerImpl implements AccountController {

    private final AccountManager accountManager;

    @Override
    @PostMapping("/account")
    public void addAccount(@RequestBody AccountDto accountDto) throws ApplicationBaseException {
        accountManager.addAccount(accountDto);
    }

    @Override
    @GetMapping("/account/{username}")
    public AccountDto getAccount(@PathVariable String username) throws ApplicationBaseException {
        return accountManager.getAccount(username);
    }
}
