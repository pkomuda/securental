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
@Table(name = "account")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public @Data class Account extends BaseAuditEntity {

    @Pattern(regexp = ApplicationProperties.EMAIL_REGEX)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "email", nullable = false, length = 32, unique = true, updatable = false)
    private String email;

    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "first_name", nullable = false, length = 32)
    private String firstName;

    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "last_name", nullable = false, length = 32)
    private String lastName;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @NotNull
    @Column(name = "confirmed", nullable = false)
    private Boolean confirmed;

    @Column(name = "login_initialization_counter")
    private Integer loginInitializationCounter;

    @Column(name = "failed_authentication_counter")
    private Integer failedAuthenticationCounter;

    @Column(name = "confirmation_token")
    private String confirmationToken;

    @Column(name = "last_successful_authentication")
    private LocalDateTime lastSuccessfulAuthentication;

    @Column(name = "last_failed_authentication")
    private LocalDateTime lastFailedAuthentication;

    @Column(name = "last_authentication_ip_address")
    private String lastAuthenticationIpAddress;

    @Column(name = "preferred_language", length = 32)
    private String preferredLanguage;

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
