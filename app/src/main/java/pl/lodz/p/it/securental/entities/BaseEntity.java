package pl.lodz.p.it.securental.entities;

import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@MappedSuperclass
public abstract @Data class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @NotNull
    @Setter(lombok.AccessLevel.NONE)
    private Long id;

    @Version
    @Setter(lombok.AccessLevel.NONE)
    private Long version;
}
