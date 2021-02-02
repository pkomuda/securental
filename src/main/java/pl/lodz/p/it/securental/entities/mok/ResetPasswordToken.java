package pl.lodz.p.it.securental.entities.mok;

import lombok.*;
import pl.lodz.p.it.securental.entities.BaseAuditEntity;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public @Data class ResetPasswordToken extends BaseAuditEntity {

    private LocalDateTime expiration;

    private String hash;

    private Boolean used;
}
