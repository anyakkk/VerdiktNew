package vrd.base;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
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

    public CurrentTest() {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
