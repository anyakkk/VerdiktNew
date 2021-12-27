package vrd.base;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "currenttest")
public class CurrentTest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TESTID")
    private Test test;

    @JsonIgnore
    @OneToMany(mappedBy = "test", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<CurrentQuestion> questions = new HashSet<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @Column(name = "DATESTART")
    private Date dateStart;

    @Column(name = "CLOSED")
    private boolean closed;

    public boolean checkOpen() {
        long currentDate = new Date().getTime();
        long difference = (currentDate - dateStart.getTime()) / (60L*1000L);

        return (difference <= session.getTime()) && !closed;
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private Session session;

    public CurrentTest() {
    }

    public CurrentTest(Test test, User user) {
        this.test = test;
        this.user = user;
    }

    public CurrentTest(Test test) {
        this.test = test;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public Set<CurrentQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<CurrentQuestion> questions) {
        this.questions = questions;
    }

    public Session getSession() {
        return session;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
