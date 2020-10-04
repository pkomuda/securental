package pl.lodz.p.it.securental.entities.accounts;

import lombok.Data;

public @Data class MaskedPassword {

    private String combination;
    private String hash;
}
