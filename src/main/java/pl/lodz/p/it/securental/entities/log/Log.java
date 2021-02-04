package pl.lodz.p.it.securental.entities.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.securental.entities.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public @Data class Log extends BaseEntity {

    @Column(length = 1024)
    private String message;
}
