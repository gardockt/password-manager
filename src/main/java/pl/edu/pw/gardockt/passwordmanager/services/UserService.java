package pl.edu.pw.gardockt.passwordmanager.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pw.gardockt.passwordmanager.RegexCheck;
import pl.edu.pw.gardockt.passwordmanager.entities.RegistrationData;
import pl.edu.pw.gardockt.passwordmanager.entities.User;
import pl.edu.pw.gardockt.passwordmanager.entities.repositories.UserRepository;
import pl.edu.pw.gardockt.passwordmanager.security.SecurityConfiguration;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(SecurityConfiguration securityConfiguration, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = securityConfiguration.getPasswordEncoder();
    }

    private void validateUsername(String username) {
        if(username == null) {
            throw new IllegalArgumentException("Username is null");
        }
        if(!RegexCheck.isValidUsername(username)) {
            throw new IllegalArgumentException("Invalid username");
        }
    }

    private void validatePassword(String password) {
        if(password == null) {
            throw new IllegalArgumentException("Password is null");
        }
        if(!RegexCheck.isValidPassword(password)) {
            throw new IllegalArgumentException("Invalid password");
        }
    }

    public void incrementFailedAttempts(String username) {
        validateUsername(username);
        userRepository.incrementFailedAttemptsSinceLogin(username);
    }

    public void resetFailedAttempts(String username) {
        validateUsername(username);
        userRepository.resetFailedAttempts(username);
    }

    public void register(RegistrationData registrationData) {
        if(registrationData == null) {
            throw new IllegalArgumentException("Registration data is null");
        }

        String username = registrationData.getUsername();
        String accountPassword = registrationData.getAccountPassword();
        String unlockPassword = registrationData.getUnlockPassword();

        validateUsername(username);
        validatePassword(accountPassword);
        validatePassword(unlockPassword);

        userRepository.save(new User(
            username,
            passwordEncoder.encode(accountPassword),
            passwordEncoder.encode(unlockPassword)
        ));
    }

}
