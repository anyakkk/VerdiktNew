package vrd.base;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "questions")
public class Quest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "QUESTID")
    private Long id;

    @Column(name = "NAMEQ")
    String nameQuest;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TESTID")
    private Test test;

    @JsonIgnore
    @OneToMany(mappedBy = "quest", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    Set<Answer> answers = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "quests", cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    private Set<Group> groups = new HashSet<>();

    public Quest(String nameQuest, Test test, Set<Answer> answers, Set<Group> groups) {
        this.nameQuest = nameQuest;
        this.test = test;
        this.answers = answers;
        this.groups = groups;
    }

    public Quest(String nameQuest, Test test, Answer answer, Group  group) {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameQuest() {
        return nameQuest;
    }

    public void setNameQuest(String nameQuest) {
        this.nameQuest = nameQuest;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public Set<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<Answer> answers) {
        this.answers = answers;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }


}
