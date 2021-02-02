package pl.lodz.p.it.securental.entities.mor;

import lombok.*;
import pl.lodz.p.it.securental.entities.BaseEntity;

import javax.persistence.Entity;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public @Data class Status extends BaseEntity {

    private String name;
}
