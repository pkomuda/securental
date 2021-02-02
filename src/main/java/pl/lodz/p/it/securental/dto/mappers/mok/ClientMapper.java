package pl.lodz.p.it.securental.dto.mappers.mok;

import pl.lodz.p.it.securental.dto.model.mok.ClientDto;
import pl.lodz.p.it.securental.entities.mok.Client;

public class ClientMapper {

    public static ClientDto toClientDto(Client client) {
        return ClientDto.builder()
                .username(client.getAccount().getOtpCredentials().getUsername())
                .firstName(client.getAccount().getFirstName())
                .lastName(client.getAccount().getLastName())
                .email(client.getAccount().getEmail())
                .build();
    }
}
