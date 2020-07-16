package pl.lodz.p.it.securental.utils;

import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;

@Converter
@Component
@AllArgsConstructor
public class EncryptionAttributeConverter implements AttributeConverter<String, String> {

    private final Environment env;

    @Override
    public String convertToDatabaseColumn(String s) {
        return Encryptors.text(Objects.requireNonNull(env.getProperty("ENCRYPTION_KEY")),
                Objects.requireNonNull(env.getProperty("ENCRYPTION_SALT"))).encrypt(s);
    }

    @Override
    public String convertToEntityAttribute(String s) {
        return Encryptors.text(Objects.requireNonNull(env.getProperty("ENCRYPTION_KEY")),
                Objects.requireNonNull(env.getProperty("ENCRYPTION_SALT"))).decrypt(s);
    }
}
