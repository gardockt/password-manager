package pl.edu.pw.gardockt.passwordmanager.entities.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pw.gardockt.passwordmanager.entities.Password;
import pl.edu.pw.gardockt.passwordmanager.entities.User;

import java.util.List;

public interface PasswordRepository extends JpaRepository<Password, Long> {

    List<Password> findByUser(User user);

}
