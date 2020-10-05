package pl.lodz.p.it.securental.entities.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.securental.entities.BaseEntity;
import pl.lodz.p.it.securental.utils.JsonAttributeConverter;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Lob;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public @Data class Credentials extends BaseEntity {

    @Lob
    @Convert(converter = JsonAttributeConverter.class)
    private List<MaskedPassword> maskedPasswords = new ArrayList<>();
}
