package pl.lodz.p.it.securental.entities.accounts;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.lodz.p.it.securental.entities.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@EqualsAndHashCode(callSuper = false)
public @Data class Password extends BaseEntity {

    private String combination;
    private String hash;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id", nullable = false, updatable = false)
    private Account account;
}
