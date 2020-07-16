package pl.lodz.p.it.securental.controllers.accounts;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.it.securental.dto.accounts.AccountDto;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.managers.accounts.AccountManager;

@Slf4j
@CrossOrigin
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
