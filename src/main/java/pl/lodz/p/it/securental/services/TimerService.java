package pl.lodz.p.it.securental.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infinispan.Cache;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.annotations.RequiresNewTransaction;
import pl.lodz.p.it.securental.entities.Log;
import pl.lodz.p.it.securental.repositories.LogRepository;
import pl.lodz.p.it.securental.repositories.mok.BlacklistedJwtRepository;

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

    @Scheduled(fixedDelayString = "${log.schedule}")
    public void moveLogsToDatabase() {
        long start = System.currentTimeMillis();
        logRepository.saveAll(
                logCache.keySet().stream()
                        .map(log -> Log.builder()
                                .message(log)
                                .build())
                        .collect(Collectors.toList())
        );
        logCache.clear();
        log.warn("moveLogsToDatabase: " + (System.currentTimeMillis() - start)/1000.0);
    }

    @Scheduled(fixedDelayString = "${jwt.schedule}")
    public void clearExpiredBlacklistedJwts() {
        long start = System.currentTimeMillis();
        blacklistedJwtRepository.deleteAllByExpirationBefore(LocalDateTime.now());
        log.warn("clearExpiredBlacklistedJwts: " + (System.currentTimeMillis() - start)/1000.0);
    }
}
