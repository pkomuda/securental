package pl.lodz.p.it.securental.entities.accounts;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("EMPLOYEE")
@EqualsAndHashCode(callSuper = true)
public @Data class Employee extends AccessLevel {
}
