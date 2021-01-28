package pl.lodz.p.it.securental.dto.model.mor;

import lombok.Builder;
import lombok.Data;
import pl.lodz.p.it.securental.dto.model.mok.ClientDto;
import pl.lodz.p.it.securental.dto.model.mop.CarDto;

@Builder
public @Data class ReservationDto {

    private String number;
    private String startDate;
    private String endDate;
    private String price;
    private String status;
    private ClientDto clientDto;
    private CarDto carDto;
    private String signature;
}
