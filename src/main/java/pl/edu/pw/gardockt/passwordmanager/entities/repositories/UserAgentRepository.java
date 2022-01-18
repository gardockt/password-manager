package pl.edu.pw.gardockt.passwordmanager.entities.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.pw.gardockt.passwordmanager.entities.UserAgent;

public interface UserAgentRepository extends JpaRepository<UserAgent, Long> {

	@Query("SELECT ua FROM UserAgent ua WHERE ua.userAgent = :userAgent")
	UserAgent getUserAgentByString(@Param("userAgent") String userAgent);

}
