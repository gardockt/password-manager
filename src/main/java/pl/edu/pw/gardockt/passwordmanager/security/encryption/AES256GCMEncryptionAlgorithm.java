package pl.edu.pw.gardockt.passwordmanager.security.encryption;

import pl.edu.pw.gardockt.passwordmanager.security.PasswordConfiguration;

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
import java.util.Arrays;
import java.util.Base64;

public class AES256GCMEncryptionAlgorithm implements EncryptionAlgorithm {

    // TODO: fix key/message containing dialects (result varying on locale)

    private static final String keyFactoryAlgorithm = "PBKDF2WithHmacSHA256";
    private static final String keyAlgorithm = "AES";
    private static final String algorithm = "AES/GCM/NoPadding";

    private static final int keyLength = 256;
    private static final int ivLength = 12;
    private static final int saltLength = 16;
    private static final int tagLength = 16 * 8;

    private static final int iterationCount = 65536;
    private static final int targetLength = PasswordConfiguration.MAX_LENGTH + 1;

    private SecretKeySpec generateKeySpec(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, keyLength);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(keyFactoryAlgorithm);
        byte[] key = keyFactory.generateSecret(keySpec).getEncoded();
        return new SecretKeySpec(key, keyAlgorithm);
    }

    // padding in order to hide password's length

    private byte[] padMessage(byte[] message) {
        byte[] paddedMessage = Arrays.copyOf(message, targetLength);
        int paddingLength = targetLength - message.length;
        Arrays.fill(paddedMessage, message.length, targetLength, (byte) paddingLength);
        return paddedMessage;
    }

    private byte[] unpadMessage(byte[] message) {
        return Arrays.copyOf(message, targetLength - message[targetLength - 1]);
    }

    public String encrypt(String message, String password, byte[] iv) throws Exception {
        if(message.length() >= targetLength) {
            throw new IllegalArgumentException("Message is too long");
        }

        byte[] messageBytes = padMessage(message.getBytes(StandardCharsets.UTF_8));

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[saltLength];
        random.nextBytes(salt);

        Cipher cipher = Cipher.getInstance(algorithm);

        GCMParameterSpec parameters = new GCMParameterSpec(tagLength, iv);

        cipher.init(Cipher.ENCRYPT_MODE, generateKeySpec(password, salt), parameters);
        byte[] encryptedMessage = cipher.doFinal(messageBytes);

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
        byte[] message = cipher.doFinal(encryptedMessage);

        message = unpadMessage(message);

        return new String(message);
    }

}
