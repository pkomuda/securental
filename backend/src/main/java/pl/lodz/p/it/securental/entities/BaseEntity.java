package pl.lodz.p.it.securental.entities;

import lombok.Data;
import lombok.Getter;
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
    @Getter(lombok.AccessLevel.NONE)
    @Setter(lombok.AccessLevel.NONE)
    private Long version;
}
