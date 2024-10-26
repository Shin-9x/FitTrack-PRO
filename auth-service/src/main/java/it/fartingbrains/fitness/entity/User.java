package it.fartingbrains.fitness.entity;

import it.fartingbrains.fitness.common.constant.TableConstants;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = TableConstants.USER_TABLE_NAME)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;

    public User() { }

    public User(
            long id, String username, String password, String email,
            LocalDateTime createDate, LocalDateTime modifiedDate
    ) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.createDate = createDate;
        this.modifiedDate = modifiedDate;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getCreateDate() { return createDate; }
    public void setCreateDate(LocalDateTime createDate) { this.createDate = createDate; }

    public LocalDateTime getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(LocalDateTime modifiedDate) { this.modifiedDate = modifiedDate; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", createDate=" + createDate +
                ", modifiedDate=" + modifiedDate +
                '}';
    }
}
