package pl.edu.pw.gardockt.passwordmanager.entities.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.pw.gardockt.passwordmanager.entities.LoginHistory;
import pl.edu.pw.gardockt.passwordmanager.entities.User;
import pl.edu.pw.gardockt.passwordmanager.entities.UserAgent;

import javax.transaction.Transactional;
import java.sql.Timestamp;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {

	@Query("UPDATE LoginHistory lh SET lh.count = lh.count + 1, lh.lastAccess = :lastAccess" +
		" WHERE lh.user = :user AND lh.ip = :ip AND lh.userAgent = :userAgent")
	@Modifying
	@Transactional
	int incrementLoginCount(@Param("user") User user, @Param("ip") String ip,
							@Param("userAgent") UserAgent userAgent, @Param("lastAccess") Timestamp lastAccess);

}
