package pl.lodz.p.it.securental.entities.mos;

import lombok.*;
import pl.lodz.p.it.securental.entities.BaseAuditEntity;
import pl.lodz.p.it.securental.entities.mor.Reservation;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "car")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public @Data class Car extends BaseAuditEntity {

    @EqualsAndHashCode.Include
    @Column(name = "number", length = 32)
    private String number;

    @Column(name = "make", length = 32)
    private String make;

    @Column(name = "model", length = 32)
    private String model;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "production_year")
    private Integer productionYear;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "active")
    private Boolean active;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 32)
    private Category category;

    @OneToMany
    @ToString.Exclude
    private List<Reservation> reservations = new ArrayList<>();

    public String toSignString() {
        return String.join(",", number, Long.toString(getVersion()));
    }
}
