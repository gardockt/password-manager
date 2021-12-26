package pl.edu.pw.gardockt.passwordmanager.entities.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pw.gardockt.passwordmanager.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

}
