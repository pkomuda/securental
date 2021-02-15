package pl.lodz.p.it.securental.entities.mok;

import lombok.*;
import pl.lodz.p.it.securental.entities.BaseAuditEntity;
import pl.lodz.p.it.securental.utils.ApplicationProperties;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public @Data class Account extends BaseAuditEntity {

    @Pattern(regexp = ApplicationProperties.EMAIL_REGEX)
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
    private Boolean active;

    @NotNull
    @Column(nullable = false)
    private Boolean confirmed;

    private Integer loginInitializationCounter;

    private Integer failedAuthenticationCounter;

    private String confirmationToken;

    private LocalDateTime lastSuccessfulAuthentication;

    private LocalDateTime lastFailedAuthentication;

    private String lastAuthenticationIpAddress;

    private String preferredLanguage;

    private String preferredColorTheme;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Credentials credentials;

    @OneToOne
    private OtpCredentials otpCredentials;

    @OneToOne(cascade = CascadeType.PERSIST)
    private AuthenticationToken authenticationToken;

    @OneToOne(cascade = CascadeType.PERSIST)
    private ResetPasswordToken resetPasswordToken;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<AccessLevel> accessLevels = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Account account = (Account) o;
        return otpCredentials.getUsername().equals(account.otpCredentials.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), otpCredentials.getUsername());
    }

    public String toSignString() {
        return String.join(",", otpCredentials.getUsername(), Long.toString(getVersion()));
    }
}
