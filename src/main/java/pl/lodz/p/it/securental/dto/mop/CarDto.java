package pl.lodz.p.it.securental.dto.mop;

import lombok.Builder;
import lombok.Data;
import pl.lodz.p.it.securental.dto.mor.ReservationDto;

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
    private List<ReservationDto> reservations;
    private String signature;
}
