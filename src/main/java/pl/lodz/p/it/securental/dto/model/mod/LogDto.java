package pl.lodz.p.it.securental.dto.model.mod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor(staticName = "of")
public @Data class LogDto {

    private String message;
}
