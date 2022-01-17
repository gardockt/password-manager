package pl.edu.pw.gardockt.passwordmanager.entities.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.pw.gardockt.passwordmanager.entities.Password;
import pl.edu.pw.gardockt.passwordmanager.entities.User;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

public interface PasswordRepository extends JpaRepository<Password, Long> {

    List<Password> findByUser(User user);

    @Query("SELECT COUNT(*) FROM Password p WHERE p.user = :user")
    int getPasswordCountByUser(@Param("user") User user);

    @Query("UPDATE Password p SET p.lastAccess = :lastAccess WHERE p = :password")
    @Modifying
    @Transactional
    void updateLastAccess(@Param("password") Password password, @Param("lastAccess") Timestamp lastAccess);

}
