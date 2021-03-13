package pl.lodz.p.it.securental.controllers.mod;

import org.springframework.data.domain.Page;
import pl.lodz.p.it.securental.dto.model.log.LogDto;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

public interface LogController {

    Page<LogDto> getAllLogs(int page, int size) throws ApplicationBaseException;
    Page<LogDto> filterLogs(String filter, int page, int size) throws ApplicationBaseException;
}
