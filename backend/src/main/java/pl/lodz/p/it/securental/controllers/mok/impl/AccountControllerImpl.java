package pl.lodz.p.it.securental.controllers.mok.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.it.securental.annotations.NeverTransaction;
import pl.lodz.p.it.securental.controllers.mok.AccountController;
import pl.lodz.p.it.securental.dto.mok.AccountDto;
import pl.lodz.p.it.securental.dto.mok.ConfirmAccountDto;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.services.mok.AccountService;
import pl.lodz.p.it.securental.utils.PagingHelper;

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
    public String register(@RequestBody AccountDto accountDto,
                           @RequestHeader("Accept-Language") String language) throws ApplicationBaseException {
        return accountService.register(accountDto, language);
    }

    @Override
    @PutMapping("/confirm")
    public void confirmAccount(@RequestBody ConfirmAccountDto confirmAccountDto) throws ApplicationBaseException {
        accountService.confirmAccount(confirmAccountDto.getToken());
    }

    @Override
    @GetMapping("/account/{username}")
    public AccountDto getAccount(@PathVariable String username) throws ApplicationBaseException {
        return accountService.getAccount(username);
    }

    @PostMapping("/add")
    public void add(@RequestBody AccountDto accountDto) throws ApplicationBaseException {
        accountService.add(accountDto);
    }

//    @GetMapping("/accounts/{filter}/{page}/{size}/{property}/{order}")
//    public Page<AccountDto> filter(@PathVariable String filter,
//                                   @PathVariable int page,
//                                   @PathVariable int size,
//                                   @PathVariable String property,
//                                   @PathVariable String order) throws ApplicationBaseException {
//        return accountService.filterAccounts(filter, new PagingHelper(page, size, property, order));
//    }
//
//    @GetMapping("/accounts/{page}/{size}/{property}/{order}")
//    public Page<AccountDto> all(@PathVariable int page,
//                                @PathVariable int size,
//                                @PathVariable String property,
//                                @PathVariable String order) throws ApplicationBaseException {
//
//        return accountService.getAllAccounts(new PagingHelper(page, size, property, order));
//    }

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
        return accountService.getAllAccounts(new PagingHelper(page, size, property, order));
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
        return accountService.filterAccounts(filter, new PagingHelper(page, size, property, order));
    }
}
