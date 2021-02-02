package pl.lodz.p.it.securental.dto.model.mop;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import pl.lodz.p.it.securental.dto.model.mor.ReservationDto;
import pl.lodz.p.it.securental.utils.ApplicationProperties;

import javax.validation.constraints.*;
import java.util.List;

@Builder
public @Data class CarDto {

    private String number;

    @NotEmpty(message = "car.make.required")
    @Size(min = 1, max = 32, message = "car.make.size")
    @Pattern(regexp = ApplicationProperties.STRING_REGEX, message = "car.make.invalid")
    private String make;

    @NotEmpty(message = "car.model.required")
    @Size(min = 1, max = 32, message = "car.model.size")
    @Pattern(regexp = ApplicationProperties.STRING_REGEX, message = "car.model.invalid")
    private String model;

    @NotEmpty(message = "car.description.required")
    @Size(min = 1, max = 255, message = "car.description.size")
    @Pattern(regexp = ApplicationProperties.STRING_REGEX, message = "car.description.invalid")
    private String description;

    @NotEmpty(message = "car.price.required")
    @Pattern(regexp = ApplicationProperties.MONEY_REGEX, message = "car.price.invalid")
    private String price;

    @NotNull(message = "car.productionYear.required")
    @Min(value = 1900, message = "car.productionYear.min")
    @Max(value = 2099, message = "car.productionYear.max")
    private Integer productionYear;

    @NotNull(message = "car.active.required")
    private Boolean active;

    @ToString.Exclude
    private List<ReservationDto> reservations;

    private String signature;
}
