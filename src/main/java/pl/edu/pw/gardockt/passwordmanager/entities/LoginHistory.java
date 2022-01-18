package pl.edu.pw.gardockt.passwordmanager.entities;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Table(name = "login_history")
public class LoginHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@NotNull
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@NotNull
	@JoinColumn(name = "user_agent_id")
	private UserAgent userAgent;

	@NotBlank
	private String ip;

	@NotNull
	private Integer count;

	@NotNull
	private Timestamp lastAccess;

	public LoginHistory() {
	}

	public LoginHistory(User user, UserAgent userAgent, String ip, int count, Timestamp lastAccess) {
		this.user = user;
		this.userAgent = userAgent;
		this.ip = ip;
		this.count = count;
		this.lastAccess = lastAccess;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserAgent getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(UserAgent userAgent) {
		this.userAgent = userAgent;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Timestamp getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(Timestamp lastAccess) {
		this.lastAccess = lastAccess;
	}
}
