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

    @Column(name = "VARIANT")
    Boolean variant;

    @Column(name = "NAMEQ")
    String nameQuest;

    @Column(name = "NEXTQ")
    String nextQuest;

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

    public Quest(String nameQuest, String nextQuest, Test test, Set<Answer> answers, Set<Group> groups, Boolean variant) {
        this.nameQuest = nameQuest;
        this.nextQuest = nextQuest;
        this.test = test;
        this.answers = answers;
        this.groups = groups;
        this.variant = variant;
    }

    public Quest(String nameQuest, String nextQuest, Test test, Answer answer, Group  group, Boolean variant) {

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

    public Boolean getVariant() {
        return variant;
    }

    public void setVariant(Boolean variant) {
        this.variant = variant;
    }

    public String getNextQuest() {
        return nextQuest;
    }

    public void setNextQuest(String nextQuest) {
        this.nextQuest = nextQuest;
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
