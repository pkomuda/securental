package pl.lodz.p.it.securental.entities.mok;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.securental.utils.ApplicationProperties;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(ApplicationProperties.ACCESS_LEVEL_ADMIN)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public @Data class Admin extends AccessLevel {

    public Admin(String name, boolean active, Account account) {
        super(name, active, account);
    }
}
