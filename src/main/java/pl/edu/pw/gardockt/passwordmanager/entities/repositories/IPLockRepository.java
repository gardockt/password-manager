package pl.edu.pw.gardockt.passwordmanager.entities.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.pw.gardockt.passwordmanager.entities.IPLock;

import java.util.Optional;

public interface IPLockRepository extends JpaRepository<IPLock, Long> {

	@Query("SELECT l FROM IPLock l WHERE l.ip = :ip")
	Optional<IPLock> getByIp(@Param("ip") String ip);

}
