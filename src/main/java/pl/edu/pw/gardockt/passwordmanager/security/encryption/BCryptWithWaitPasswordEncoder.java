package pl.edu.pw.gardockt.passwordmanager.security.encryption;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.edu.pw.gardockt.passwordmanager.security.SecurityConfiguration;

import java.security.SecureRandom;
import java.util.Random;

public class BCryptWithWaitPasswordEncoder extends BCryptPasswordEncoder {

	private final SecurityConfiguration securityConfiguration;
	private final Random random = new SecureRandom();

	public BCryptWithWaitPasswordEncoder(SecurityConfiguration securityConfiguration) {
		this.securityConfiguration = securityConfiguration;
	}

	private int getWaitTime() {
		return random.nextInt(securityConfiguration.authMaxWaitTimeMillis - securityConfiguration.authMinWaitTimeMillis + 1) + securityConfiguration.authMinWaitTimeMillis;
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		try {
			Thread.sleep(getWaitTime());
			return super.matches(rawPassword, encodedPassword);
		} catch (InterruptedException e) {
			return false;
		}
	}
}
