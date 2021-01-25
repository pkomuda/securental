package pl.lodz.p.it.securental.entities;

import lombok.*;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public @Data class BlacklistedJwt extends BaseEntity {

    @ToString.Include
    private String token;

    private LocalDateTime expiration;
}
