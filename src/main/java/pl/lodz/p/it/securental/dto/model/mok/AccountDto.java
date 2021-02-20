package pl.lodz.p.it.securental.dto.model.mok;

import lombok.Builder;
import lombok.Data;
import pl.lodz.p.it.securental.utils.ApplicationProperties;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Builder
public @Data class AccountDto {

    @NotEmpty(message = "account.username.required")
    @Size(min = 1, max = 32, message = "account.username.size")
    @Pattern(regexp = ApplicationProperties.STRING_REGEX, message = "account.username.invalid")
    private String username;

    private String password;

    private String confirmPassword;

    @NotEmpty(message = "account.email.required")
    @Size(min = 1, max = 32, message = "account.email.size")
    @Pattern(regexp = ApplicationProperties.EMAIL_REGEX, message = "account.email.invalid")
    private String email;

    @NotEmpty(message = "account.firstName.required")
    @Size(min = 1, max = 32, message = "account.firstName.size")
    @Pattern(regexp = ApplicationProperties.STRING_REGEX, message = "account.firstName.invalid")
    private String firstName;

    @NotEmpty(message = "account.lastName.required")
    @Size(min = 1, max = 32, message = "account.lastName.size")
    @Pattern(regexp = ApplicationProperties.STRING_REGEX, message = "account.lastName.invalid")
    private String lastName;

    private Boolean active;

    private Boolean confirmed;

    private List<String> accessLevels;

    private String signature;
}
