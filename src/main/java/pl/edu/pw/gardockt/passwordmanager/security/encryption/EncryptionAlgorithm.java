package pl.edu.pw.gardockt.passwordmanager.security.encryption;

public interface EncryptionAlgorithm {
    String encrypt(String message, String password) throws Exception;
    String decrypt(String cryptogram, String password) throws Exception;
}
