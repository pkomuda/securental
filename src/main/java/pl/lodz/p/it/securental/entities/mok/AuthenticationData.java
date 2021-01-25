package pl.lodz.p.it.securental.entities.mok;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.lodz.p.it.securental.entities.BaseAuditEntity;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@EqualsAndHashCode(callSuper = true)
public @Data class AuthenticationData extends BaseAuditEntity {

    private LocalDateTime lastSuccessfulAuthentication;

    private LocalDateTime lastFailedAuthentication;
}
