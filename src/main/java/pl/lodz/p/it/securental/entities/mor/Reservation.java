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
@Table(name = "reservation")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public @Data class Reservation extends BaseAuditEntity {

    @EqualsAndHashCode.Include
    @Column(name = "number")
    private String number;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "price")
    private BigDecimal price;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private Status status;

    @ManyToOne
    private Client client;

    @ManyToOne
    private Car car;

    @ElementCollection
    @CollectionTable(name = "reservation_received_image_urls")
    private List<String> receivedImageUrls = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "reservation_finished_image_urls")
    private List<String> finishedImageUrls = new ArrayList<>();

    public String toSignString() {
        return String.join(",", number, Long.toString(getVersion()));
    }
}
