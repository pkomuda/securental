package pl.lodz.p.it.securental.utils;

import org.springframework.stereotype.Component;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.ResourceLoadingException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;

import static pl.lodz.p.it.securental.utils.ApplicationProperties.KEYSTORE_PASSWORD;
import static pl.lodz.p.it.securental.utils.StringUtils.base64;
import static pl.lodz.p.it.securental.utils.StringUtils.decode;

@Component
public class SignatureUtils {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public SignatureUtils() throws ApplicationBaseException {
        try {
            KeyStore privateKeystore = KeyStore.getInstance("PKCS12");
            privateKeystore.load(this.getClass().getResourceAsStream("/private_keystore.p12"), KEYSTORE_PASSWORD);
            privateKey = (PrivateKey) privateKeystore.getKey("senderKeyPair", KEYSTORE_PASSWORD);

            KeyStore publicKeystore = KeyStore.getInstance("PKCS12");
            publicKeystore.load(this.getClass().getResourceAsStream("/public_keystore.p12"), KEYSTORE_PASSWORD);
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
            return base64(signature.sign());
        } catch (GeneralSecurityException e) {
            throw new ResourceLoadingException(e);
        }
    }

    public boolean verify(String message, String received) throws ApplicationBaseException {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(message.getBytes(StandardCharsets.UTF_8));
            return signature.verify(decode(received));
        } catch (GeneralSecurityException e) {
            throw new ResourceLoadingException(e);
        }
    }
}
