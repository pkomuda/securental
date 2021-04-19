package pl.lodz.p.it.securental.services.mod;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.adapters.mod.LogAdapter;
import pl.lodz.p.it.securental.aop.annotations.RequiresNewTransaction;
import pl.lodz.p.it.securental.configuration.persistence.ModConfiguration;
import pl.lodz.p.it.securental.dto.model.mod.LogDto;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.db.DatabaseConnectionException;
import pl.lodz.p.it.securental.utils.PagingHelper;

@Service
@AllArgsConstructor
@Retryable(DatabaseConnectionException.class)
@RequiresNewTransaction(ModConfiguration.MOD_TRANSACTION_MANAGER)
public class LogService {

    private final LogAdapter logAdapter;

    @PreAuthorize("hasAuthority('getAllLogs')")
    public Page<LogDto> getAllLogs(PagingHelper pagingHelper) throws ApplicationBaseException {
        return logAdapter
                .getAllLogs(pagingHelper.withoutSorting())
                .map(log -> LogDto.of(log.getMessage()));
    }

    @PreAuthorize("hasAuthority('filterLogs')")
    public Page<LogDto> filterLogs(String filter, PagingHelper pagingHelper) throws ApplicationBaseException {
        return logAdapter
                .filterLogs(filter, pagingHelper.withoutSorting())
                .map(log -> LogDto.of(log.getMessage()));
    }
}
