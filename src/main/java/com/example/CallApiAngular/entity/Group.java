package com.example.CallApiAngular.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "`group`")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String url;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group")
    @JsonIgnore
    private List<Option> options;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group")
    @JsonIgnore
    private List<Image> images;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group")
    @JsonIgnore
    private List<Banner> banners;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group")
    @JsonIgnore
    private List<Product> products;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group")
//    private Set<Banner> banners;
//
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group")
//    private Set<Image> images;
//
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group")
//    private Set<Option> options;
//
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group")
//    private Set<Product> products;


    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }


    public List<Option> getOptions() {
        return options;
    }

    public List<Image> getImages() {
        return images;
    }

    public List<Banner> getBanners() {
        return banners;
    }

    public List<Product> getProducts() {
        return products;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
