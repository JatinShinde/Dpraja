package com.spdpboss.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class WeeklyChart {

    @Id
    private Long id = 1L; // We only ever need one row in this table

    @Column(columnDefinition = "TEXT")
    private String ankChart;

    @Column(columnDefinition = "TEXT")
    private String jodiChart;

    @Column(columnDefinition = "TEXT")
    private String pattiChart;

    // Default constructor
    public WeeklyChart() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAnkChart() { return ankChart; }
    public void setAnkChart(String ankChart) { this.ankChart = ankChart; }

    public String getJodiChart() { return jodiChart; }
    public void setJodiChart(String jodiChart) { this.jodiChart = jodiChart; }

    public String getPattiChart() { return pattiChart; }
    public void setPattiChart(String pattiChart) { this.pattiChart = pattiChart; }
}