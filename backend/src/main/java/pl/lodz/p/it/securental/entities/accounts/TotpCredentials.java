package pl.lodz.p.it.securental.entities.accounts;

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
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public @Data class TotpCredentials extends BaseEntity {

    private String username;

    private String secret;

    private Integer validationCode;

    @ElementCollection
    private List<Integer> scratchCodes = new ArrayList<>();

//    @OneToOne(mappedBy = "totpCredentials")
//    private Account account;
}
