package pl.lodz.p.it.securental.entities.mok;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.securental.entities.BaseEntity;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public @Data class OtpCredentials extends BaseEntity {

    private String username;

    private String secret;

    private Integer validationCode;

    @ElementCollection
    private List<Integer> scratchCodes = new ArrayList<>();
}
