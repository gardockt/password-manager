package pl.edu.pw.gardockt.passwordmanager.services;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import pl.edu.pw.gardockt.passwordmanager.ApplicationConfiguration;
import pl.edu.pw.gardockt.passwordmanager.RegexCheck;
import pl.edu.pw.gardockt.passwordmanager.entities.*;
import pl.edu.pw.gardockt.passwordmanager.entities.repositories.IPLockRepository;
import pl.edu.pw.gardockt.passwordmanager.entities.repositories.LoginHistoryRepository;
import pl.edu.pw.gardockt.passwordmanager.entities.repositories.PasswordRepository;
import pl.edu.pw.gardockt.passwordmanager.entities.repositories.UserAgentRepository;
import pl.edu.pw.gardockt.passwordmanager.security.SecurityConfiguration;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class DatabaseService {

    private final PasswordRepository passwordRepository;
    private final UserAgentRepository userAgentRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final IPLockRepository ipLockRepository;

    private final SecurityConfiguration securityConfiguration;

    public DatabaseService(PasswordRepository passwordRepository, UserAgentRepository userAgentRepository, LoginHistoryRepository loginHistoryRepository, IPLockRepository ipLockRepository, SecurityConfiguration securityConfiguration) {
        this.passwordRepository = passwordRepository;
        this.userAgentRepository = userAgentRepository;
        this.loginHistoryRepository = loginHistoryRepository;
        this.ipLockRepository = ipLockRepository;
        this.securityConfiguration = securityConfiguration;
    }

    public List<Password> getPasswords(User user) {
        return passwordRepository.findByUser(user);
    }

    @Transactional
    public void addPassword(Password password) {
        if(password == null) {
            throw new IllegalArgumentException("Password is null");
        } else if(password.getUser() == null) {
            throw new IllegalArgumentException("User is null");
        } else if(password.getUsername() != null && !RegexCheck.containsOnlyLegalCharacters(password.getUsername())) {
            throw new IllegalArgumentException("Invalid username");
        } else if(password.getPassword() == null || !RegexCheck.isBase64(password.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        } else if(password.getDescription() == null || !RegexCheck.containsOnlyLegalCharacters(password.getDescription())) {
            throw new IllegalArgumentException("Invalid description");
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

    public List<LoginHistory> getLoginHistoryByUser(User user) {
        if(user == null) {
            throw new IllegalArgumentException("User is null");
        }

        return loginHistoryRepository.getByUser(user);
    }

    public Optional<IPLock> getIpLock(String ip) {
        if(ip == null) {
            throw new IllegalArgumentException("IP is null");
        }

        return ipLockRepository.getByIp(ip);
    }

    public void deleteIpLock(IPLock ipLock) {
        if(ipLock == null) {
            throw new IllegalArgumentException("IP lock is null");
        }

        ipLockRepository.delete(ipLock);
    }

    public void incrementFailedAttempts(String ip) {
        if(ip == null) {
            throw new IllegalArgumentException("IP is null");
        }

        IPLock ipLock = getIpLock(ip).orElse(new IPLock(ip));
        if(ipLock.getUnlockDatetime() != null) {
            return;
        }

        int newFailedAttempts = ipLock.getFailedAttempts() + 1;
        ipLock.setFailedAttempts(newFailedAttempts);
        if(newFailedAttempts >= securityConfiguration.failedAttemptsLockCount) {
            ipLock.setUnlockDatetime(new Timestamp(System.currentTimeMillis() + securityConfiguration.lockTimeMillis));
        }
        ipLockRepository.save(ipLock);
    }

}
