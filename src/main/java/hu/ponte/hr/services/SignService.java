package hu.ponte.hr.services;

import hu.ponte.hr.config.SignConfig;
import hu.ponte.hr.exception.exceptions.CustomSignatureException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * Service class for digital signing binary contents.
 */
@Service
@Slf4j
public class SignService {

    /**
     * {@link SignConfig} instance.
     */
    private SignConfig signConfig;

    /**
     * Create a Base64 encoded digital signature by a predefined private key file.
     * Throws {@link IllegalArgumentException} if any of the parameters is null.
     *
     * @param data     binary content of a file to be signed
     * @param fileName the original file name
     * @return the Base64 encoded signature
     */
    public String createSignature(byte[] data, String fileName) {
        if (data == null || fileName == null) {
            String methodName = new Object() {
            }.getClass().getEnclosingMethod().getName();
            log.error("{} method was called with null parameter(s).", methodName);
            throw new IllegalArgumentException();
        }
        try (InputStream privateKeyStream = new ClassPathResource(signConfig.getPrivateKeyPathInResources()).getInputStream()) {

            KeyFactory keyFactory = KeyFactory.getInstance(signConfig.getKeyAlgorithm());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(IOUtils.toByteArray(privateKeyStream));
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            log.info("Private key is loaded for signing file (name: {}).", fileName);

            Signature signature = Signature.getInstance(signConfig.getSignatureAlgorithm());
            signature.initSign(privateKey);
            signature.update(data);
            byte[] signatureData = signature.sign();
            log.info("New signature is created for file (name: {}).", fileName);

            return Base64.getEncoder().encodeToString(signatureData);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | SignatureException |
                 InvalidKeyException ex) {
            String msg = "Something went wrong during signing.";
            log.error(msg, ex);
            throw new CustomSignatureException("Something went wrong during signing.");
        }
    }

    /**
     * Binds a {@link SignConfig} instance externally. It provides testing functionality without loading a full application context.
     *
     * @param signConfig config instance to be bound
     */
    @Autowired
    protected void setSignConfig(SignConfig signConfig) {
        this.signConfig = signConfig;
    }

}
