package pl.lodz.p.it.securental.entities.accounts;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.lodz.p.it.securental.entities.BaseEntity;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@EqualsAndHashCode(callSuper = true)
public @Data class AuthenticationData extends BaseEntity {

    private LocalDateTime lastSuccessfulAuthentication;

    private LocalDateTime lastFailedAuthentication;
}
