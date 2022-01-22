package pl.edu.pw.gardockt.passwordmanager.entities.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.gardockt.passwordmanager.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query("UPDATE User u SET u.failedAttemptsSinceLogin = u.failedAttemptsSinceLogin + 1 WHERE u.username = :username")
    @Modifying
    @Transactional
    void incrementFailedAttemptsSinceLogin(@Param("username") String username);

    @Query("UPDATE User u SET u.failedAttemptsSinceLogin = 0 WHERE u.username = :username")
    @Modifying
    @Transactional
    void resetFailedAttempts(@Param("username") String username);

}
