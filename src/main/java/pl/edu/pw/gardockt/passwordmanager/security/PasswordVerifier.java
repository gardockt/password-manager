package pl.edu.pw.gardockt.passwordmanager.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.pw.gardockt.passwordmanager.entities.User;

@Component
public class PasswordVerifier {

    @Autowired
    private SecurityConfiguration securityConfiguration;

    public boolean verifyUnlockPassword(User user, String unlockPassword) {
        if(securityConfiguration.getPasswordEncoder().matches(unlockPassword, user.getUnlockPassword())) {
            return true;
        } else {
            // TODO: increment incorrect attempt count
            return false;
        }
    }

}
