package pl.edu.pw.gardockt.passwordmanager.services;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import pl.edu.pw.gardockt.passwordmanager.ApplicationConfiguration;
import pl.edu.pw.gardockt.passwordmanager.entities.LoginHistory;
import pl.edu.pw.gardockt.passwordmanager.entities.Password;
import pl.edu.pw.gardockt.passwordmanager.entities.User;
import pl.edu.pw.gardockt.passwordmanager.entities.UserAgent;
import pl.edu.pw.gardockt.passwordmanager.entities.repositories.LoginHistoryRepository;
import pl.edu.pw.gardockt.passwordmanager.entities.repositories.PasswordRepository;
import pl.edu.pw.gardockt.passwordmanager.entities.repositories.UserAgentRepository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

@Service
public class DatabaseService {

    private final PasswordRepository passwordRepository;
    private final UserAgentRepository userAgentRepository;
    private final LoginHistoryRepository loginHistoryRepository;

    public DatabaseService(PasswordRepository passwordRepository, UserAgentRepository userAgentRepository, LoginHistoryRepository loginHistoryRepository) {
        this.passwordRepository = passwordRepository;
        this.userAgentRepository = userAgentRepository;
        this.loginHistoryRepository = loginHistoryRepository;
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

    public void updatePasswordLastAccess(Password password) {
        if(password == null) {
            throw new IllegalArgumentException("Password is null");
        }

        passwordRepository.updateLastAccess(password, new Timestamp(System.currentTimeMillis()));
    }

    public void addLoginHistory(User user, String ip, String userAgentString) {
        if(user == null) {
            throw new IllegalArgumentException("User is null");
        } else if(ip == null) {
            throw new IllegalArgumentException("IP is null");
        } else if(userAgentString == null) {
            throw new IllegalArgumentException("User agent is null");
        }

        UserAgent userAgent = userAgentRepository.getUserAgentByString(userAgentString);
        if(userAgent == null) {
            userAgent = new UserAgent(userAgentString);
            userAgentRepository.save(userAgent);
        }

        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
        if(loginHistoryRepository.incrementLoginCount(user, ip, userAgent, currentDatetime) == 0) {
            loginHistoryRepository.save(new LoginHistory(user, userAgent, ip, 1, currentDatetime));
        }
    }

}
