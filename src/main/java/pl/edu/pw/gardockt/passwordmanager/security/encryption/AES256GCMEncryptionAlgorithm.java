package pl.edu.pw.gardockt.passwordmanager.security.encryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class AES256GCMEncryptionAlgorithm implements EncryptionAlgorithm {

    // TODO: fix key/message containing dialects (result varying on locale)
    // TODO: fix potential timing attack
    // TODO: mask message length

    private static final String keyFactoryAlgorithm = "PBKDF2WithHmacSHA256";
    private static final String keyAlgorithm = "AES";
    private static final String algorithm = "AES/GCM/NoPadding";

    private static final int keyLength = 256;
    private static final int ivLength = 12;
    private static final int saltLength = 16;
    private static final int tagLength = 16 * 8;

    private static final int iterationCount = 65536;

    private SecretKeySpec generateKeySpec(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, keyLength);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(keyFactoryAlgorithm);
        byte[] key = keyFactory.generateSecret(keySpec).getEncoded();
        return new SecretKeySpec(key, keyAlgorithm);
    }

    public String encrypt(String message, String password) throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[saltLength];
        random.nextBytes(salt);

        Cipher cipher = Cipher.getInstance(algorithm);

        // TODO: GCMParameterSpec

        cipher.init(Cipher.ENCRYPT_MODE, generateKeySpec(password, salt));
        cipher.update(message.getBytes(StandardCharsets.UTF_8));
        byte[] encryptedMessage = cipher.doFinal();
        byte[] iv = cipher.getIV();

        byte[] cryptogram = new byte[ivLength + saltLength + encryptedMessage.length];
        System.arraycopy(iv, 0, cryptogram, 0, ivLength);
        System.arraycopy(salt, 0, cryptogram, ivLength, saltLength);
        System.arraycopy(encryptedMessage, 0, cryptogram, ivLength + saltLength, encryptedMessage.length);

        return new String(Base64.getEncoder().encode(cryptogram));
    }

    public String decrypt(String cryptogram, String password) throws Exception {
        byte[] cryptogramBytes = Base64.getDecoder().decode(cryptogram);
        byte[] iv = new byte[ivLength];
        byte[] salt = new byte[saltLength];
        byte[] encryptedMessage = new byte[cryptogramBytes.length - (ivLength + saltLength)];

        System.arraycopy(cryptogramBytes, 0, iv, 0, ivLength);
        System.arraycopy(cryptogramBytes, ivLength, salt, 0, saltLength);
        System.arraycopy(cryptogramBytes, ivLength + saltLength, encryptedMessage, 0, encryptedMessage.length);

        Cipher cipher = Cipher.getInstance(algorithm);

        GCMParameterSpec parameters = new GCMParameterSpec(tagLength, iv);

        cipher.init(Cipher.DECRYPT_MODE, generateKeySpec(password, salt), parameters);
        cipher.update(encryptedMessage);
        byte[] message = cipher.doFinal();

        return new String(message);
    }

}
