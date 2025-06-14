package com.example.CallApiAngular.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "options")
public class Option {

    @Id
    @Column(name = "href")
    private String href;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    public String gethref() {
        return href;
    }

    public void sethref(String href) {
        this.href = href;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
