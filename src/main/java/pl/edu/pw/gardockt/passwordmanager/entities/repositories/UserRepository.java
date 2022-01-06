package pl.edu.pw.gardockt.passwordmanager.entities.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.gardockt.passwordmanager.entities.User;

import java.sql.Timestamp;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query("UPDATE User u SET u.failedAttempts = u.failedAttempts + 1 WHERE u.username = :username AND u.failedAttempts < :failedAttemptsLockCount - 1")
    @Modifying
    @Transactional
    int incrementFailedAttempts(@Param("username") String username, @Param("failedAttemptsLockCount") Integer failedAttemptsLockCount);

    @Query("UPDATE User u SET u.failedAttempts = 0 WHERE u.username = :username")
    @Modifying
    @Transactional
    void resetFailedAttempts(@Param("username") String username);

    @Query("UPDATE User u SET u.unlockDatetime = :unlockDatetime WHERE u.username = :username")
    @Modifying
    @Transactional
    void lock(@Param("username") String username, @Param("unlockDatetime") Timestamp unlockDatetime);

    @Query("UPDATE User u SET u.unlockDatetime = NULL, u.failedAttempts = 0 WHERE u.username = :username")
    @Modifying
    @Transactional
    void unlock(@Param("username") String username);

}
