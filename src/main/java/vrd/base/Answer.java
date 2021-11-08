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
    @JoinColumn(name = "QUESTID")
    private Quest quest;

    @Column(name = "NAMECONTENT")
    private String answerCont;

    @Column(name = "NAMEORDER")
    private byte order;

    public Answer() {
    }

    public Answer(byte order, Quest quest, String cont) {
        this.order = order;
        this.quest = quest;
        this.answerCont = cont;
    }

    public byte getOrder() {
        return order;
    }

    public void setOrder(byte order) {
        this.order = order;
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

}
