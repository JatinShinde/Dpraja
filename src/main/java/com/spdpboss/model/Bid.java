package com.spdpboss.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mobile;         // Who placed the bid
    private String gameName;       // E.g., SRIDEVI, TIME BAZAR
    private String bidDate;        // E.g., 2026-06-26
    private String gameSession;    // OPEN or CLOSE
    private String bidType;       // SINGLE, JODI, SINGLE_PANNA, etc.
    private String bidNumber;      // The number they are playing on
    private int amount;            // Money put on the bid
    private String status;         // PENDING, WON, LOST
    private double payoutAmount;   // Amount won if status is WON

    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getGameName() { return gameName; }
    public void setGameName(String gameName) { this.gameName = gameName; }

    public String getBidDate() { return bidDate; }
    public void setBidDate(String bidDate) { this.bidDate = bidDate; }

    public String getGameSession() { return gameSession; }
    public void setGameSession(String gameSession) { this.gameSession = gameSession; }

    public String getBidType() { return bidType; }
    public void setBidType(String bidType) { this.bidType = bidType; }

    public String getBidNumber() { return bidNumber; }
    public void setBidNumber(String bidNumber) { this.bidNumber = bidNumber; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getPayoutAmount() { return payoutAmount; }
    public void setPayoutAmount(double payoutAmount) { this.payoutAmount = payoutAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}