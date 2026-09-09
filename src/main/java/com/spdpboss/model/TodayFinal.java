package com.spdpboss.model;

import jakarta.persistence.*;

@Entity
@Table(name = "today_final")
public class TodayFinal {
    
    @Id
    private Long id; // IDs used: 1-Puna, 2-Kalyan, 3-Main, 4-Notice, 5-Weekly Jodi, 6-Weekly Patti, 7-Weekly Line
    
    @Column(length = 2000) // Increased length for long weekly prediction lists
    private String content;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean hidden = false;

    // 1. Default Constructor (Required by JPA/Hibernate)
    public TodayFinal() {}

    // 2. Custom Constructor (Fixes the "Undefined Constructor" error in Controller)
    public TodayFinal(Long id, String content) {
        this.id = id;
        this.content = content;
        this.hidden = false;
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}