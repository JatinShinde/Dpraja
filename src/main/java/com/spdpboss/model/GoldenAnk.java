package com.spdpboss.model;

import jakarta.persistence.*;


@Entity
@Table(name = "golden_ank")
public class GoldenAnk {
	
    
    @Id
    private Long id; // We will manually use ID 1L
    
    
    @Column(name = "ank_val")
    private String value; // The Golden Number (e.g., "5")

    // Default Constructor
    public GoldenAnk() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}