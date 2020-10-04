package pl.lodz.p.it.securental.entities.accounts;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.lodz.p.it.securental.entities.BaseEntity;
import pl.lodz.p.it.securental.utils.JsonAttributeConverter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public @Data class Account extends BaseEntity {

    @NotNull
    @Size(min = 1, max = 32)
    @Column(nullable = false, length = 32, unique = true, updatable = false)
    @EqualsAndHashCode.Include
    private String username;

    @Email
    @NotNull
    @Size(min = 1, max = 32)
    @Column(nullable = false, length = 32, unique = true, updatable = false)
    private String email;

    @NotNull
    @Size(min = 1, max = 32)
    @Column(nullable = false, length = 32)
    private String firstName;

    @NotNull
    @Size(min = 1, max = 32)
    @Column(nullable = false, length = 32)
    private String lastName;

    @NotNull
    @Column(nullable = false)
    private boolean active;

    @NotNull
    @Column(nullable = false)
    private boolean confirmed;

    @Lob
    @Convert(converter = JsonAttributeConverter.class)
    private List<MaskedPassword> maskedPasswords = new ArrayList<>();
}
