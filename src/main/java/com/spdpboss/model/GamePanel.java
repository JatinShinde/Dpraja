package com.spdpboss.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "game_panels")
public class GamePanel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String gameName;
	private String date;
	private String openPanel; // e.g., 123
	private String jodi; // e.g., 45
	private String closePanel; // e.g., 678

	// Getters and Setters
}
