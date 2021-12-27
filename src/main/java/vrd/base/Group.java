package vrd.base;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "QUESTGROUP")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TESTID")
    private Test test;

    @ManyToMany(mappedBy = "groups", fetch = FetchType.EAGER)
    private Set<Quest> quests = new HashSet<>();

    @Column(name = "NAMEORDER")
    private int order;

    @Column(name = "QUESTCOUNT")
    int questCount;

    public Group() {
    }

    public Group(Test test, Set<Quest> quests, int questCount) {
        this.test = test;
        this.quests = quests;
        this.questCount = questCount;
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

    public Set<Quest> getQuests() {
        return quests;
    }

    public void setQuests(Set<Quest> quests) {
        this.quests = quests;
    }

    public int getOrder() {
        return order;

    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getQuestCount() {
        return questCount;
    }

    public void setQuestCount(int questCount) {
        this.questCount = questCount;
    }


}
