package pl.edu.pw.gardockt.passwordmanager.entities;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Table(name = "passwords")
public class Password {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String description;

    private Timestamp lastAccess;

    public Password() {
    }

    public Password(Long id, User user, String password, String description) {
        this.id = id;
        this.user = user;
        this.password = password;
        this.description = description;
        this.lastAccess = null;
    }

    public Password(Long id, User user, String username, String password, String description, Timestamp lastAccess) {
        this.id = id;
        this.user = user;
        this.username = username;
        this.password = password;
        this.description = description;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(Timestamp lastAccess) {
        this.lastAccess = lastAccess;
    }

    public Password clone() {
        try {
            return (Password) super.clone();
        } catch(CloneNotSupportedException e) {
            return new Password(id, user, username, password, description, lastAccess);
        }
    }

}
