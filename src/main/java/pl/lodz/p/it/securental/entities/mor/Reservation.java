package pl.lodz.p.it.securental.entities.mor;

import lombok.*;
import pl.lodz.p.it.securental.entities.BaseAuditEntity;
import pl.lodz.p.it.securental.entities.mok.Client;
import pl.lodz.p.it.securental.entities.mop.Car;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public @Data class Reservation extends BaseAuditEntity {

    @EqualsAndHashCode.Include
    private String number;

//    @Future
    private LocalDateTime startDate;

//    @Future
    private LocalDateTime endDate;

    private BigDecimal price;

    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @ManyToOne
    private Client client;

    @ManyToOne
    private Car car;

    @ElementCollection/*(fetch = FetchType.EAGER)*/
    private List<String> receivedImageUrls = new ArrayList<>();

    @ElementCollection
    private List<String> finishedImageUrls = new ArrayList<>();

    public String toSignString() {
        return String.join(",", number, Long.toString(getVersion()));
    }
}
