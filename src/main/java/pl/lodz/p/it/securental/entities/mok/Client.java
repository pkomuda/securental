package pl.lodz.p.it.securental.entities.mok;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import static pl.lodz.p.it.securental.utils.ApplicationProperties.ACCESS_LEVEL_CLIENT;

@Entity
@DiscriminatorValue(ACCESS_LEVEL_CLIENT)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public @Data class Client extends AccessLevel {

    public Client(String name, boolean active) {
        super(name, active);
    }
}
