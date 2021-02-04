package pl.lodz.p.it.securental.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infinispan.Cache;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.aop.annotations.RequiresNewTransaction;
import pl.lodz.p.it.securental.entities.log.Log;
import pl.lodz.p.it.securental.repositories.mok.BlacklistedJwtRepository;
import pl.lodz.p.it.securental.repositories.log.LogRepository;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@Service
@EnableScheduling
@AllArgsConstructor
@RequiresNewTransaction
public class TimerService {

    private final Cache<String, String> logCache;
    private final LogRepository logRepository;
    private final BlacklistedJwtRepository blacklistedJwtRepository;

    @Scheduled(fixedDelayString = "#{60000 * ${log.schedule}}")
    public void persistLogs() {
        long start = System.currentTimeMillis();
        logRepository.saveAll(
                logCache.keySet().stream()
                        .map(Log::new)
                        .collect(Collectors.toList())
        );
        logCache.clear();
        log.info("Completed scheduled task TimerService::persistLogs | Execution time: " + (System.currentTimeMillis() - start)/1000.0 + "s");
    }

    @Scheduled(fixedDelayString = "#{60000 * ${jwt.schedule}}")
    public void clearExpiredBlacklistedJwts() {
        long start = System.currentTimeMillis();
        blacklistedJwtRepository.deleteAllByExpirationBefore(LocalDateTime.now());
        log.info("Completed scheduled task TimerService::clearExpiredBlacklistedJwts | Execution time: " + (System.currentTimeMillis() - start)/1000.0 + "s");
    }
}
