package com.spdpboss.model; 

import jakarta.persistence.*;

@Entity
public class FreeAnk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String finalAnk;
    private String cutAnk;
    public FreeAnk() {
        // Default constructor required by JPA
    }

    public FreeAnk(String name) {
        this.name = name;
    }
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getFinalAnk() { return finalAnk; }
    public void setFinalAnk(String finalAnk) { this.finalAnk = finalAnk; }
    public String getCutAnk() { return cutAnk; }
    public void setCutAnk(String cutAnk) { this.cutAnk = cutAnk; }
}