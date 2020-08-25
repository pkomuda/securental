package pl.lodz.p.it.securental.entities.accounts;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pl.lodz.p.it.securental.entities.BaseEntity;
import pl.lodz.p.it.securental.utils.EncryptionAttributeConverter;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public @Data class MaskedPassword extends BaseEntity {

    @Convert(converter = EncryptionAttributeConverter.class)
    @EqualsAndHashCode.Include
    private String combination;

    private String hash;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id", nullable = false, updatable = false)
    @ToString.Exclude
    private Account account;
}
