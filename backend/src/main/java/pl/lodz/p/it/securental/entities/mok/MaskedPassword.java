package pl.lodz.p.it.securental.entities.mok;

import lombok.Data;

public @Data class MaskedPassword {

    private String combination;
    private String hash;
}
