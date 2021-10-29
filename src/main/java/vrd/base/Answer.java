package vrd.base;


import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;


@Entity
@Table(name = "answers")
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ANSWERID")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TESTID")
    private Test test;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "QUESTID")
    private Quest quest;

    @Column(name = "NAMECONTENT")
    private String answerCont;

    public Answer() {
    }

    public Answer(long id, Test test, Quest quest, String cont) {
        this.id = id;
        this.test = test;
        this.quest = quest;
        this.answerCont = cont;
    }

    public String getAnswerCont() {
        return answerCont;
    }

    public void setAnswerCont(String answerCont) {
        this.answerCont = answerCont;
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

    public Quest getQuest() {
        return quest;
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
    }
}
