package pl.lodz.p.it.securental.entities.mor;

import lombok.*;
import pl.lodz.p.it.securental.entities.BaseAuditEntity;
import pl.lodz.p.it.securental.entities.mok.Client;
import pl.lodz.p.it.securental.entities.mop.Car;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public @Data class Reservation extends BaseAuditEntity {

    @EqualsAndHashCode.Include
    private String number;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private BigDecimal price;

    @ManyToOne
    private Status status;

    @ManyToOne
    private Client client;

    @ManyToOne
    private Car car;

    public String toSignString() {
        return String.join(",", number, Long.toString(getVersion()));
    }
}
