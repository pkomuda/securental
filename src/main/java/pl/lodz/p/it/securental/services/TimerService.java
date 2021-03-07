package pl.lodz.p.it.securental.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infinispan.Cache;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.securental.aop.annotations.RequiresNewTransaction;
import pl.lodz.p.it.securental.configuration.persistence.LogConfiguration;
import pl.lodz.p.it.securental.configuration.persistence.MokConfiguration;
import pl.lodz.p.it.securental.configuration.persistence.MorConfiguration;
import pl.lodz.p.it.securental.entities.log.Log;
import pl.lodz.p.it.securental.entities.mor.Reservation;
import pl.lodz.p.it.securental.entities.mor.Status;
import pl.lodz.p.it.securental.repositories.log.LogRepository;
import pl.lodz.p.it.securental.repositories.mok.BlacklistedJwtRepository;
import pl.lodz.p.it.securental.repositories.mor.ReservationRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@EnableScheduling
@AllArgsConstructor
public class TimerService {

    private final Cache<String, String> logCache;
    private final LogRepository logRepository;
    private final BlacklistedJwtRepository blacklistedJwtRepository;
    private final ReservationRepository reservationRepository;

    @Scheduled(fixedDelayString = "#{60000 * ${log.schedule}}")
    @RequiresNewTransaction(LogConfiguration.LOG_TRANSACTION_MANAGER)
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
    @RequiresNewTransaction(MokConfiguration.MOK_TRANSACTION_MANAGER)
    public void clearExpiredBlacklistedJwts() {
        long start = System.currentTimeMillis();
        blacklistedJwtRepository.deleteAllByExpirationBefore(LocalDateTime.now());
        log.info("Completed scheduled task TimerService::clearExpiredBlacklistedJwts | Execution time: " + (System.currentTimeMillis() - start)/1000.0 + "s");
    }

    @Scheduled(fixedDelayString = "#{60000 * ${reservation.schedule}}")
    @RequiresNewTransaction(MorConfiguration.MOR_TRANSACTION_MANAGER)
    public void cancelStartedReservations() {
        long start = System.currentTimeMillis();
        List<Reservation> reservations = reservationRepository.findAllByStartDateBeforeAndStatusIn(LocalDateTime.now(), Collections.singletonList(Status.NEW));
        for (Reservation reservation : reservations) {
            long timeSinceStart = ChronoUnit.MINUTES.between(reservation.getStartDate(), LocalDateTime.now());
            long reservationTime = ChronoUnit.MINUTES.between(reservation.getStartDate(), reservation.getEndDate());
            if (((double) reservationTime/timeSinceStart) < 6) {
                reservation.setStatus(Status.CANCELLED);
            }
        }
        log.info("Completed scheduled task TimerService::cancelStartedReservations | Execution time: " + (System.currentTimeMillis() - start)/1000.0 + "s");
    }
}
