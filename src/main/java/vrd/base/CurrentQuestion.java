package vrd.base;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "currentquestion")
public class CurrentQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TESTID")
    private CurrentTest test;

    int inTestIndex;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "QUESTID")
    private Quest quest;

    public CurrentQuestion() {
    }

    public CurrentQuestion(CurrentTest test, int inTestIndex, Quest quest) {
        this.test = test;
        this.inTestIndex = inTestIndex;
        this.quest = quest;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CurrentTest getTest() {
        return test;
    }

    public void setTest(CurrentTest test) {
        this.test = test;
    }

    public int getInTestIndex() {
        return inTestIndex;
    }

    public void setInTestIndex(int inTestIndex) {
        this.inTestIndex = inTestIndex;
    }

    public Quest getQuest() {
        return quest;
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
    }
}
