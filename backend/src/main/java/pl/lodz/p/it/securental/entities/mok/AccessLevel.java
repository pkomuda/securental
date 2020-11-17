package pl.lodz.p.it.securental.entities.mok;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.securental.entities.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@EqualsAndHashCode(callSuper = true)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "name")
public @Data class AccessLevel extends BaseEntity {

    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "name", nullable = false, length = 32, updatable = false, insertable = false) //TODO insertable?
    private String name;

    @NotNull
    @Column(nullable = false)
    private boolean active;
}
