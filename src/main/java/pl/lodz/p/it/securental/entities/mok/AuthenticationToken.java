package pl.lodz.p.it.securental.entities.mok;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.securental.entities.BaseAuditEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authentication_token")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public @Data class AuthenticationToken extends BaseAuditEntity {

    @ElementCollection
    @CollectionTable(name = "authentication_token_combination")
    private List<Integer> combination = new ArrayList<>();

    @Column(name = "expiration")
    private LocalDateTime expiration;
}
