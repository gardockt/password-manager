package pl.edu.pw.gardockt.passwordmanager.security.encryption;

import pl.edu.pw.gardockt.passwordmanager.security.SecurityConfiguration;

import java.security.SecureRandom;
import java.util.Random;

public class AES256GCMWithWaitEncryptionAlgorithm extends AES256GCMEncryptionAlgorithm {

	private final SecurityConfiguration securityConfiguration;
	private final Random random = new SecureRandom();

	public AES256GCMWithWaitEncryptionAlgorithm(SecurityConfiguration securityConfiguration) {
		this.securityConfiguration = securityConfiguration;
	}

	private int getWaitTime() {
		return random.nextInt(securityConfiguration.authMaxWaitTimeMillis - securityConfiguration.authMinWaitTimeMillis + 1) + securityConfiguration.authMinWaitTimeMillis;
	}

	@Override
	public String decrypt(String cryptogram, String password) throws Exception {
		Thread.sleep(getWaitTime());
		return super.decrypt(cryptogram, password);
	}

}
