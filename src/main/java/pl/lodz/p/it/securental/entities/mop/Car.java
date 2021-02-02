package pl.lodz.p.it.securental.entities.mop;

import lombok.*;
import pl.lodz.p.it.securental.entities.BaseAuditEntity;
import pl.lodz.p.it.securental.entities.mor.Reservation;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public @Data class Car extends BaseAuditEntity {

    @EqualsAndHashCode.Include
    private String number;

    private String make;

    private String model;

    private String description;

    private Integer productionYear;

    private BigDecimal price;

    private Boolean active;

    @OneToMany
    @ToString.Exclude
    private List<Reservation> reservations = new ArrayList<>();

    public String toSignString() {
        return String.join(",", number, Long.toString(getVersion()));
    }
}
