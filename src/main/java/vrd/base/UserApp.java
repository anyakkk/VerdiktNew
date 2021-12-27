package vrd.base;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "USERAPP")
public class UserApp {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "IDAPP")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private Session session;

    @Column(name = "STATUS")
    private StatusApp status;

    public UserApp() {
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

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public StatusApp getStatus() {
        return status;
    }

    public void setStatus(StatusApp status) {
        this.status = status;
    }
}
