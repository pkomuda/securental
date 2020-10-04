package pl.lodz.p.it.securental.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.lodz.p.it.securental.entities.accounts.MaskedPassword;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.List;

@Converter
public class JsonAttributeConverter implements AttributeConverter<List<MaskedPassword>, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<MaskedPassword> maskedPasswords) {
        try {
            return mapper.writeValueAsString(maskedPasswords);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public List<MaskedPassword> convertToEntityAttribute(String s) {
        try {
            return mapper.readValue(s, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
