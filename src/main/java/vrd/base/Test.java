package vrd.base;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "tableTest")
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "TESTID")
    private Long id;

    @Column(name = "NAMET")
    String nametest;

    @JsonIgnore
    @OneToMany(mappedBy = "test", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    Set<Quest> quests = new HashSet<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USERID")
    private User user;

    public Test() {
    }

    public Test(String nametest, Set<Quest> quests, User user) {
        this.nametest = nametest;
        this.quests = quests;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNametest() {
        return nametest;
    }

    public void setNametest(String nametest) {
        this.nametest = nametest;
    }

    public Set<Quest> getQuests() {
        return quests;
    }

    public void setQuests(Set<Quest> quests) {
        this.quests = quests;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
