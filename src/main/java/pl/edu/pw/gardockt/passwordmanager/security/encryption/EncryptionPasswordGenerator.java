package pl.edu.pw.gardockt.passwordmanager.security.encryption;

public class EncryptionPasswordGenerator {

	// in order to prevent repeated IV + encryption password across accounts
	public static String generate(String enteredPassword, String extraData) {
		return enteredPassword + "|" + extraData;
	}

}
