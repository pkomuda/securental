package pl.lodz.p.it.securental.dto.mop;

import lombok.Builder;
import lombok.Data;

@Builder
public @Data class CarDto {

    private String number;
    private String make;
    private String model;
    private String description;
    private int productionYear;
    private double price;
    private boolean active;
    private String signature;
}
