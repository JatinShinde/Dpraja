package com.spdpboss.model;

import java.time.LocalDate;

public class DayCell {
    private LocalDate date;
    private String dayName;
    private boolean active;
    private GameHistory record;

    public DayCell() {}

    public DayCell(LocalDate date, String dayName, boolean active, GameHistory record) {
        this.date = date;
        this.dayName = dayName;
        this.active = active;
        this.record = record;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public GameHistory getRecord() {
        return record;
    }

    public void setRecord(GameHistory record) {
        this.record = record;
    }
}
