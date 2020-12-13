package pl.lodz.p.it.securental.entities.mok;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import static pl.lodz.p.it.securental.utils.ApplicationProperties.ACCESS_LEVEL_ADMIN;

@Entity
@DiscriminatorValue(ACCESS_LEVEL_ADMIN)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public @Data class Admin extends AccessLevel {

    public Admin(String name, boolean active) {
        super(name, active);
    }
}
