package com.chisom.commons.util;

import com.google.crypto.tink.*;
import com.google.crypto.tink.aead.AeadConfig;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CryptoUtil {

    private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();

    static {
        try {
            AeadConfig.register(); // Initializes Tink
        } catch (Exception e) {
            throw new ExceptionInInitializerError(
                    "Failed to initialize Tink: " + e);
        }
    }

    /**
     * Encrypts a given plain text using AES encryption with the provided key.
     * <p>
     * The method uses Tink's AES encryption functionalities to securely encrypt
     * the input string. It returns the cipher text as a byte array.
     *
     * @param plainText the plain text to be encrypted, must not be null
     * @param key       the encryption key in its serialized KeysetHandle format, must not be null
     * @return a byte array containing the encrypted data (cipher text)
     * @throws GeneralSecurityException if an error occurs during the encryption process
     */
    public static byte[] aesEncryptAsBytes(String plainText, String key) throws GeneralSecurityException {
        try {
            KeysetHandle keysetHandle = TinkJsonProtoKeysetFormat.parseKeyset(key, InsecureSecretKeyAccess.get());
            // Use the simplified primitive retrieval method.
            Aead aead = keysetHandle.getPrimitive(RegistryConfiguration.get(), Aead.class);
            return aead.encrypt(plainText.getBytes(UTF_8), null);
        } catch (Exception e) {
            String msg = "An error occurred while encrypting the plain text, possible reason: %s";
            msg = String.format(msg, e);
            logger.error(msg);
            throw new GeneralSecurityException(msg, e);
        }
    }

    /**
     * Decrypts the given Base64-encoded cipher using the provided AES key (cleartext JSON format).
     *
     * @param base64EncodedCipher The encoded cipher text to decrypt.
     * @param key                 The decryption key (in Tink JSON format).
     * @return The decrypted plain text.
     */
    public static String aesDecrypt(String base64EncodedCipher, String key) throws GeneralSecurityException {
        Objects.requireNonNull(base64EncodedCipher, "Cipher text cannot be null.");
        Objects.requireNonNull(key, "Key cannot be null.");

        try {
            KeysetHandle keysetHandle = TinkJsonProtoKeysetFormat.parseKeyset(key, InsecureSecretKeyAccess.get());
            Aead aead = keysetHandle.getPrimitive(RegistryConfiguration.get(), Aead.class);
            byte[] decrypted = aead.decrypt(BASE64_DECODER.decode(base64EncodedCipher), null);
            return new String(decrypted, UTF_8);
        } catch (Exception e) {
            String msg = "An error occurred while decrypting the cipher text, possible reason: %s";
            msg = String.format(msg, e);
            logger.error(msg);
            throw new GeneralSecurityException(msg, e);
        }
    }
}