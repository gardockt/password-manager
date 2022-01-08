package pl.edu.pw.gardockt.passwordmanager.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pw.gardockt.passwordmanager.entities.RegistrationData;
import pl.edu.pw.gardockt.passwordmanager.entities.User;
import pl.edu.pw.gardockt.passwordmanager.entities.repositories.UserRepository;
import pl.edu.pw.gardockt.passwordmanager.security.SecurityConfiguration;

import java.sql.Timestamp;

@Service
public class UserService {

    private final SecurityConfiguration securityConfiguration;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(SecurityConfiguration securityConfiguration, UserRepository userRepository) {
        this.securityConfiguration = securityConfiguration;
        this.userRepository = userRepository;
        this.passwordEncoder = securityConfiguration.getPasswordEncoder();
    }

    public void incrementFailedAttempts(String username) {
        if(username == null) {
            throw new IllegalArgumentException("Username is null");
        }

        // if no rows are updated, we hit the attempt limit
        // this function is called only after user is found, so we don't have to worry about no username found
        userRepository.incrementFailedAttemptsSinceLogin(username);
        if(userRepository.incrementFailedAttemptsSinceUnlock(username, securityConfiguration.failedAttemptsLockCount) == 0) {
            lock(username);
        }
    }

    public void resetFailedAttempts(String username) {
        if(username == null) {
            throw new IllegalArgumentException("Username is null");
        }
        userRepository.resetFailedAttempts(username);
    }

    public void lock(String username) {
        if(username == null) {
            throw new IllegalArgumentException("Username is null");
        }
        userRepository.lock(username, new Timestamp(System.currentTimeMillis() + securityConfiguration.lockTimeMillis));
    }

    public void unlock(String username) {
        if(username == null) {
            throw new IllegalArgumentException("Username is null");
        }
        userRepository.unlock(username);
    }

    public void register(RegistrationData registrationData) {
        if(registrationData == null) {
            throw new IllegalArgumentException("Registration data is null");
        }

        String username = registrationData.getUsername();
        String accountPassword = registrationData.getAccountPassword();
        String unlockPassword = registrationData.getUnlockPassword();

        if(username == null) {
            throw new IllegalArgumentException("Username is null");
        }
        if(accountPassword == null) {
            throw new IllegalArgumentException("Account password is null");
        }
        if(unlockPassword == null) {
            throw new IllegalArgumentException("Unlock password is null");
        }

        userRepository.save(new User(
            username,
            passwordEncoder.encode(accountPassword),
            passwordEncoder.encode(unlockPassword)
        ));
    }

}
