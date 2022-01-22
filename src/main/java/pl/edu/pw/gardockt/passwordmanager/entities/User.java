package pl.edu.pw.gardockt.passwordmanager.entities;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    private String accountPassword;

    @NotBlank
    private String unlockPassword;

    @NotBlank
    private String roles;

    @NotNull
    private Integer failedAttemptsSinceLogin;

    public User() {
    }

    public User(String username, String accountPassword, String unlockPassword) {
        this.username = username;
        this.accountPassword = accountPassword;
        this.unlockPassword = unlockPassword;
        this.roles = "user";
        this.failedAttemptsSinceLogin = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccountPassword() {
        return accountPassword;
    }

    public void setAccountPassword(String accountPassword) {
        this.accountPassword = accountPassword;
    }

    public String getUnlockPassword() {
        return unlockPassword;
    }

    public void setUnlockPassword(String unlockPassword) {
        this.unlockPassword = unlockPassword;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public Integer getFailedAttemptsSinceLogin() {
        return failedAttemptsSinceLogin;
    }

    public void setFailedAttemptsSinceLogin(Integer failedAttemptsSinceLogin) {
        this.failedAttemptsSinceLogin = failedAttemptsSinceLogin;
    }

}
