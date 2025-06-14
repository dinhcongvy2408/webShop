package com.example.CallApiAngular.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "images")
public class Image {

    @Id
    private String src;

    private String href;

    private String alt;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
    public String getsrc() {
        return src;
    }

    public void setsrc(String src) {
        this.src = src;
    }

    public String gethref() {
        return href;
    }

    public void sethref(String href) {
        this.href = href;
    }

    public String getalt() {
        return alt;
    }

    public void setalt(String alt) {
        this.alt = alt;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
