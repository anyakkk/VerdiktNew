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

    @Column(name = "TEXT", length = 8000)
    String text;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TESTID")
    private Test test;

    @OneToMany(mappedBy = "quest", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    Set<Answer> answers = new HashSet<>();

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    private Set<Group> groups = new HashSet<>();

    public Quest() {
    }

    public Quest(String text, Test test, Boolean variant) {
        this.text = text;
        this.test = test;
        this.variant = variant;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getVariant() {
        return variant;
    }

    public void setVariant(Boolean variant) {
        this.variant = variant;
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

    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
    }

}
