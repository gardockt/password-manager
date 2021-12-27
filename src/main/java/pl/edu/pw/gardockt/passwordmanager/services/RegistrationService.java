package pl.edu.pw.gardockt.passwordmanager.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pw.gardockt.passwordmanager.entities.User;
import pl.edu.pw.gardockt.passwordmanager.entities.repositories.UserRepository;
import pl.edu.pw.gardockt.passwordmanager.security.SecurityConfiguration;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(SecurityConfiguration securityConfiguration, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = securityConfiguration.getPasswordEncoder();
    }

    public void register(String username, String accountPassword, String unlockPassword) {
        userRepository.save(new User(username, passwordEncoder.encode(accountPassword), passwordEncoder.encode(unlockPassword)));
    }

}
