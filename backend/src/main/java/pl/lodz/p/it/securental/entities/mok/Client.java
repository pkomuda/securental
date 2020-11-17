package pl.lodz.p.it.securental.entities.mok;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("CLIENT")
@EqualsAndHashCode(callSuper = true)
public @Data class Client extends AccessLevel {
}
