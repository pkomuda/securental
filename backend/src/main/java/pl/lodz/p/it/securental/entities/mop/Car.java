package pl.lodz.p.it.securental.entities.mop;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.securental.entities.BaseEntity;

import javax.persistence.Entity;

@Entity
@Builder
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public @Data class Car extends BaseEntity {

    @EqualsAndHashCode.Include
    private String number;

    private String make;

    private String model;

    private String description;

    private int productionYear;

    private double price;

    private boolean active;

    public String toSignString() {
        return String.join(",", number, Long.toString(getVersion()));
    }
}
