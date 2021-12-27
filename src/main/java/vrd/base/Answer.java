package vrd.base;


import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;


@Entity
@Table(name = "answers")
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ANSWERID", nullable = false)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "QUESTID")
    private Quest quest;

    @Column(name = "NAMECONTENT")
    private String answerCont;

    @Column(name = "NAMEORDER")
    private int order;

    @Column(name = "CORRECT")
    boolean correct;

    public Answer() {
    }

    public Answer(int order, Quest quest, String cont, Boolean correct) {
        this.order = order;
        this.quest = quest;
        this.answerCont = cont;
        this.correct = correct;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnswerCont() {
        return answerCont;
    }

    public void setAnswerCont(String answerCont) {
        this.answerCont = answerCont;
    }

    public Quest getQuest() {
        return quest;
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
    }


    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
