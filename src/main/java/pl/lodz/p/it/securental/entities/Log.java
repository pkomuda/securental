package pl.lodz.p.it.securental.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public @Data class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @NotNull
    @Setter(lombok.AccessLevel.NONE)
    private Long id;

    @Lob
    private String message;
}
