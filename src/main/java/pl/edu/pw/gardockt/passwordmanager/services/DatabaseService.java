package pl.edu.pw.gardockt.passwordmanager.services;

import org.springframework.stereotype.Service;
import pl.edu.pw.gardockt.passwordmanager.entities.Password;
import pl.edu.pw.gardockt.passwordmanager.entities.User;
import pl.edu.pw.gardockt.passwordmanager.entities.repositories.PasswordRepository;

import java.util.List;

@Service
public class DatabaseService {

    private final PasswordRepository passwordRepository;

    public DatabaseService(PasswordRepository passwordRepository) {
        this.passwordRepository = passwordRepository;
    }

    public List<Password> getPasswords(User user) {
        return passwordRepository.findByUser(user);
    }

    public void addPassword(Password password) {
        if(password == null) {
            throw new IllegalArgumentException("Password is null");
        }
        passwordRepository.save(password);
    }

}
