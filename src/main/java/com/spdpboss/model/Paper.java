package com.spdpboss.model;

import jakarta.persistence.*;

@Entity
@Table(name = "papers") // Must match exactly what you typed in MySQL
public class Paper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    @Column(name = "image_url") // Matches your MySQL column name
    private String imageUrl;

    private String type; // DAILY or WEEKLY

    // IMPORTANT: Make sure you have empty constructor and getters/setters!
    public Paper() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}