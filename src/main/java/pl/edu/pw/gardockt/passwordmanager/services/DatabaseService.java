package pl.edu.pw.gardockt.passwordmanager.services;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import pl.edu.pw.gardockt.passwordmanager.ApplicationConfiguration;
import pl.edu.pw.gardockt.passwordmanager.entities.Password;
import pl.edu.pw.gardockt.passwordmanager.entities.User;
import pl.edu.pw.gardockt.passwordmanager.entities.repositories.PasswordRepository;

import javax.transaction.Transactional;
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

    @Transactional
    public void addPassword(Password password) {
        if(password == null) {
            throw new IllegalArgumentException("Password is null");
        }

        passwordRepository.save(password);
        if(passwordRepository.getPasswordCountByUser(password.getUser()) > ApplicationConfiguration.MAX_STORED_PASSWORDS_COUNT) {
            throw new DataIntegrityViolationException("Max password count reached");
        }
    }

}
