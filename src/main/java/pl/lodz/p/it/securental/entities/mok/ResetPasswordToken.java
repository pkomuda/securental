package pl.lodz.p.it.securental.entities.mok;

import lombok.*;
import pl.lodz.p.it.securental.entities.BaseAuditEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "reset_password_token")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public @Data class ResetPasswordToken extends BaseAuditEntity {

    @Column(name = "expiration")
    private LocalDateTime expiration;

    @Column(name = "hash")
    private String hash;

    @Column(name = "used")
    private Boolean used;
}
