package pl.edu.pw.gardockt.passwordmanager.entities;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Table(name = "ip_locks")
public class IPLock {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank
	private String ip;

	@NotNull
	private Integer failedAttempts;

	private Timestamp unlockDatetime;

	@NotNull
	private Timestamp resetDatetime;

	public IPLock() {

	}

	public IPLock(String ip, Timestamp resetDatetime) {
		this.ip = ip;
		this.failedAttempts = 0;
		this.resetDatetime = resetDatetime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getFailedAttempts() {
		return failedAttempts;
	}

	public void setFailedAttempts(Integer failedAttempts) {
		this.failedAttempts = failedAttempts;
	}

	public Timestamp getUnlockDatetime() {
		return unlockDatetime;
	}

	public void setUnlockDatetime(Timestamp unlockDatetime) {
		this.unlockDatetime = unlockDatetime;
	}

	public Timestamp getResetDatetime() {
		return resetDatetime;
	}

	public void setResetDatetime(Timestamp resetDatetime) {
		this.resetDatetime = resetDatetime;
	}
}
