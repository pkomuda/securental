package pl.lodz.p.it.securental.entities.accounts;

import lombok.*;
import pl.lodz.p.it.securental.entities.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static pl.lodz.p.it.securental.utils.ApplicationProperties.EMAIL_REGEXP;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public @Data class Account extends BaseEntity {

    @Pattern(regexp = EMAIL_REGEXP)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(nullable = false, length = 32, unique = true, updatable = false)
    private String email;

    @NotNull
    @Column(nullable = false)
    private String fullPassword;

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

    private String confirmationToken;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Credentials credentials;

    @OneToOne
    private OtpCredentials otpCredentials;

    @OneToOne(cascade = CascadeType.PERSIST)
    private AuthenticationToken authenticationToken;
}
