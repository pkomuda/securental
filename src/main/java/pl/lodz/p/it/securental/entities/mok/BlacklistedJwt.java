package pl.lodz.p.it.securental.entities.mok;

import lombok.*;
import pl.lodz.p.it.securental.entities.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "blacklisted_jwt")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public @Data class BlacklistedJwt extends BaseEntity {

    @ToString.Include
    @Column(name = "token")
    private String token;

    @Column(name = "expiration")
    private LocalDateTime expiration;
}
