package pl.lodz.p.it.securental.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import pl.lodz.p.it.securental.entities.mok.MaskedPassword;
import pl.lodz.p.it.securental.exceptions.db.DataProcessingException;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.List;

@Converter
public class BlobAttributeConverter implements AttributeConverter<List<MaskedPassword>, byte[]> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    @SneakyThrows
    public byte[] convertToDatabaseColumn(List<MaskedPassword> maskedPasswords) {
        try {
            return mapper.writeValueAsBytes(maskedPasswords);
        } catch (JsonProcessingException e) {
            throw new DataProcessingException(e);
        }
    }

    @Override
    @SneakyThrows
    public List<MaskedPassword> convertToEntityAttribute(byte[] byteArray) {
        try {
            return mapper.readValue(byteArray, new TypeReference<>() {});
        } catch (IOException e) {
            throw new DataProcessingException(e);
        }
    }
}
