package vrd.base;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "SESSION")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "IDSESSION")
    private Long id;

    @Column(name = "SESSIONNAME")
    private String nameSession;

    @JsonIgnore
    @OneToMany(mappedBy = "session", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<UserApp> userApps = new HashSet<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER")
    private User user;

    @Column(name = "DATE")
    private Date dateStart;

    @Column(name = "TIME")
    private int time;

    @Column(name = "STARTED")
    private boolean started = false;

    @Column(name = "SESSIONTIME")
    private int sessionTime;


    public boolean checkOpen() {
        long currentDate = new Date().getTime();
        long difference = (currentDate - dateStart.getTime()) / (60L * 1000L);
        return (difference <= sessionTime);
    }

    public void closeSession() {
        sessionTime = 0;
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TEST")
    private Test test;


    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "CURRENTTEST")
    private Set<CurrentTest> tests;

    public Session() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameSession() {
        return nameSession;
    }

    public void setNameSession(String nameSession) {
        this.nameSession = nameSession;
    }

    public Set<UserApp> getUserApps() {
        return userApps;
    }

    public void setUserApps(Set<UserApp> userApps) {
        this.userApps = userApps;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public Set<CurrentTest> getTests() {
        return tests;
    }

    public void setTests(Set<CurrentTest> tests) {
        this.tests = tests;
    }

    public boolean getStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public int getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(int sessionTime) {
        this.sessionTime = sessionTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
