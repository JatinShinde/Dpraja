package com.spdpboss.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "game_history")
public class GameHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gameName;
    private LocalDate resultDate;
    private String openPanel;
    private String openAnk;
    private String jodi;
    
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getOpenPanel() {
		return openPanel;
	}
	public void setOpenPanel(String openPanel) {
		this.openPanel = openPanel;
	}
	public String getOpenAnk() {
		return openAnk;
	}
	public void setOpenAnk(String openAnk) {
		this.openAnk = openAnk;
	}
	public String getJodi() {
		return jodi;
	}
	public void setJodi(String jodi) {
		this.jodi = jodi;
	}
	public String getCloseAnk() {
		return closeAnk;
	}
	public void setCloseAnk(String closeAnk) {
		this.closeAnk = closeAnk;
	}
	public String getClosePanel() {
		return closePanel;
	}
	public void setClosePanel(String closePanel) {
		this.closePanel = closePanel;
	}
	private String closeAnk;
    private String closePanel;

    // Standard Getters and Setters
    public String getGameName() { return gameName; }
    public void setGameName(String gameName) { this.gameName = gameName; }
    public LocalDate getResultDate() { return resultDate; }
    public void setResultDate(LocalDate resultDate) { this.resultDate = resultDate; }

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