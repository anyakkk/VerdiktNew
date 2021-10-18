package vrd.base;

import javax.persistence.*;

@Entity
@Table(name = "usergroups")
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USERGROUPID")
    private Long id;

    @Column(name = "NAMEUSERGROUP")
    private String nameuserGroup;

    @Column(name = "YEAR")
    private int year;

    public UserGroup() {
    }

    public UserGroup(String nameuserGroup, int year) {
        this.nameuserGroup = nameuserGroup;
        this.year = year;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameuserGroup() {
        return nameuserGroup;
    }

    public void setNameuserGroup(String nameuserGroup) {
        this.nameuserGroup = nameuserGroup;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
