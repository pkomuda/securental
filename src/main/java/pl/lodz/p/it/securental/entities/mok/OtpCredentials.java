package pl.lodz.p.it.securental.entities.mok;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.securental.entities.BaseAuditEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "otp_credentials")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public @Data class OtpCredentials extends BaseAuditEntity {

    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "username", nullable = false, length = 32, unique = true, updatable = false)
    private String username;

    @Column(name = "secret")
    private String secret;

    @Column(name = "validation_code")
    private Integer validationCode;
}
