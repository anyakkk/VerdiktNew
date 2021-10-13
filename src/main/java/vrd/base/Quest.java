package vrd.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
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
}
