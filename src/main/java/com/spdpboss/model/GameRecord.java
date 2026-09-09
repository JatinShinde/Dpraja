package com.spdpboss.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "game_records")
public class GameRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gameName;
    private LocalDate date;

    // Full Panel Strings (Required by MainController)
    private String openPanel;  
    private String closePanel; 
    
    // Jodi Number and Color Logic
    private String jodi;
    private String color; // Stores 'RED' or 'BLACK'

    // Date Range for Weekly Charts
    private String startDate; 
    private String endDate;

    // Individual Panel Digits (For Vertical Chart Layouts)
    private String open1;
    private String open2;
    private String open3;
    private String close1;
    private String close2;
    private String close3;

    // --- GETTERS AND SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGameName() { return gameName; }
    public void setGameName(String gameName) { this.gameName = gameName; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getOpenPanel() { return openPanel; }
    public void setOpenPanel(String openPanel) { this.openPanel = openPanel; }

    public String getClosePanel() { return closePanel; }
    public void setClosePanel(String closePanel) { this.closePanel = closePanel; }

    public String getJodi() { return jodi; }
    public void setJodi(String jodi) { this.jodi = jodi; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getOpen1() { return open1; }
    public void setOpen1(String open1) { this.open1 = open1; }

    public String getOpen2() { return open2; }
    public void setOpen2(String open2) { this.open2 = open2; }

    public String getOpen3() { return open3; }
    public void setOpen3(String open3) { this.open3 = open3; }

    public String getClose1() { return close1; }
    public void setClose1(String close1) { this.close1 = close1; }

    public String getClose2() { return close2; }
    public void setClose2(String close2) { this.close2 = close2; }

    public String getClose3() { return close3; }
    public void setClose3(String close3) { this.close3 = close3; }

    public boolean isRedJodi() {
        if (jodi == null || jodi.trim().length() != 2) {
            return false;
        }
        String cleanJodi = jodi.trim();
        char c1 = cleanJodi.charAt(0);
        char c2 = cleanJodi.charAt(1);
        if (!Character.isDigit(c1) || !Character.isDigit(c2)) {
            return false;
        }
        return (c1 == c2) || (Math.abs(c1 - c2) == 5);
    }
}