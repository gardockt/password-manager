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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final int failedAttemptsLockCount = 10;
    private final long lockTimeMillis = 5 * 60 * 1000;

    public UserService(SecurityConfiguration securityConfiguration, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = securityConfiguration.getPasswordEncoder();
    }

    public void incrementFailedAttempts(String username) {
        // if no rows are updated, we hit the attempt limit
        // this function is called only after user is found, so we don't have to worry about no username found
        if(userRepository.incrementFailedAttempts(username, failedAttemptsLockCount) == 0) {
            lock(username);
        }
    }

    public void resetFailedAttempts(String username) {
        userRepository.resetFailedAttempts(username);
    }

    public void lock(String username) {
        userRepository.lock(username, new Timestamp(System.currentTimeMillis() + lockTimeMillis));
    }

    public void unlock(String username) {
        userRepository.unlock(username);
    }

    public void register(RegistrationData registrationData) {
        userRepository.save(new User(
                registrationData.getUsername(),
                passwordEncoder.encode(registrationData.getAccountPassword()),
                passwordEncoder.encode(registrationData.getUnlockPassword())
        ));
    }

}
