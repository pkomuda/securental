package pl.lodz.p.it.securental.entities.accounts;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.lodz.p.it.securental.entities.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
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

    @NotNull
    @Column(nullable = false)
    private String password;

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

    @OneToMany(mappedBy = "account", cascade = CascadeType.PERSIST)
    private List<Password> passwords = new ArrayList<>();
}
