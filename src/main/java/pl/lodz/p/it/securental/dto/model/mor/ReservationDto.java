package pl.lodz.p.it.securental.dto.model.mor;

import lombok.Builder;
import lombok.Data;
import pl.lodz.p.it.securental.dto.model.mok.ClientDto;
import pl.lodz.p.it.securental.dto.model.mop.CarDto;
import pl.lodz.p.it.securental.utils.ApplicationProperties;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Builder
public @Data class ReservationDto {

    private String number;

    @NotEmpty(message = "reservation.startDate.required")
    @Pattern(regexp = ApplicationProperties.DATETIME_REGEX, message = "reservation.startDate.invalid")
    private String startDate;

    @NotEmpty(message = "reservation.endDate.required")
    @Pattern(regexp = ApplicationProperties.DATETIME_REGEX, message = "reservation.endDate.invalid")
    private String endDate;

    @NotEmpty(message = "reservation.price.required")
    @Pattern(regexp = ApplicationProperties.MONEY_REGEX, message = "reservation.price.invalid")
    private String price;

    @NotEmpty(message = "reservation.status.required")
    @Pattern(regexp = ApplicationProperties.STATUS_REGEX, message = "reservation.status.invalid")
    private String status;

    private ClientDto clientDto;

    private CarDto carDto;

    private String signature;
}
