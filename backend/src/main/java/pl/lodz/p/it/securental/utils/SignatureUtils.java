package pl.lodz.p.it.securental.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.ResourceLoadingException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;

import static pl.lodz.p.it.securental.utils.ApplicationProperties.KEYSTORE_PASSWORD;
import static pl.lodz.p.it.securental.utils.StringUtils.decodeBase64Url;
import static pl.lodz.p.it.securental.utils.StringUtils.encodeBase64Url;

@Component
public class SignatureUtils {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public SignatureUtils() throws ApplicationBaseException {
        try {
            KeyStore privateKeystore = KeyStore.getInstance("PKCS12");
            privateKeystore.load(new ClassPathResource("public/private_keystore.p12").getInputStream(), KEYSTORE_PASSWORD);
            privateKey = (PrivateKey) privateKeystore.getKey("senderKeyPair", KEYSTORE_PASSWORD);

            KeyStore publicKeystore = KeyStore.getInstance("PKCS12");
            publicKeystore.load(new ClassPathResource("public/public_keystore.p12").getInputStream(), KEYSTORE_PASSWORD);
            Certificate certificate = publicKeystore.getCertificate("receiverKeyPair");
            publicKey = certificate.getPublicKey();
        } catch (GeneralSecurityException | IOException e) {
            throw new ResourceLoadingException(e);
        }
    }

    public String sign(String message) throws ApplicationBaseException {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(message.getBytes(StandardCharsets.UTF_8));
            return encodeBase64Url(signature.sign());
        } catch (GeneralSecurityException e) {
            throw new ResourceLoadingException(e);
        }
    }

    public boolean verify(String message, String received) throws ApplicationBaseException {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(message.getBytes(StandardCharsets.UTF_8));
            return signature.verify(decodeBase64Url(received));
        } catch (GeneralSecurityException e) {
            throw new ResourceLoadingException(e);
        }
    }
}
