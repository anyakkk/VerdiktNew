package vrd.base;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USERID")
    private Long id;

    @Column(name = "NAMEUSER")
    private String username;

    @Column(name = "USERPASSWORD")
    private String password;

    @Column(name = "PASSWORDCONFIRM")
    private String passwordConfirm;

    @Column(name = "ROLE")
    private String role;


    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<Test> tests = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<CurrentTest> currentTests = new HashSet<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "NAMEUSERGROUP")
    private UserGroup userGroup;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.tests = tests;
        this.currentTests = currentTests;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String userpassword) {
        this.password = userpassword;
    }

    public Set<Test> getTests() {
        return tests;
    }

    public void setTests(Set<Test> tests) {
        this.tests = tests;
    }

    public Set<CurrentTest> getCurrentTests() {
        return currentTests;
    }

    public void setCurrentTests(Set<CurrentTest> currentTests) {
        this.currentTests = currentTests;
    }
}

