package pl.lodz.p.it.securental.controllers.mod.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.p.it.securental.aop.annotations.NeverTransaction;
import pl.lodz.p.it.securental.controllers.mod.LogController;
import pl.lodz.p.it.securental.dto.model.log.LogDto;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.services.mod.LogService;
import pl.lodz.p.it.securental.utils.PagingHelper;

@RestController
@AllArgsConstructor
@NeverTransaction
public class LogControllerImpl implements LogController {

    private final LogService logService;

    @Override
    @GetMapping("/logs/{page}/{size}")
    @PreAuthorize("hasAuthority('getAllLogs')")
    public Page<LogDto> getAllLogs(@PathVariable int page,
                                   @PathVariable int size) throws ApplicationBaseException {
        return logService.getAllLogs(new PagingHelper(page, size));
    }

    @Override
    @GetMapping("/logs/{filter}/{page}/{size}")
    @PreAuthorize("hasAuthority('filterLogs')")
    public Page<LogDto> filterLogs(@PathVariable String filter,
                                   @PathVariable int page,
                                   @PathVariable int size) throws ApplicationBaseException {
        return logService.filterLogs(filter, new PagingHelper(page, size));
    }
}
