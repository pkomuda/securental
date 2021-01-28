package pl.lodz.p.it.securental.dto.model.mop;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import pl.lodz.p.it.securental.dto.model.mor.ReservationDto;

import java.util.List;

@Builder
public @Data class CarDto {

    private String number;
    private String make;
    private String model;
    private String description;
    private int productionYear;
    private String price;
    private boolean active;
    @ToString.Exclude
    private List<ReservationDto> reservations;
    private String signature;
}
