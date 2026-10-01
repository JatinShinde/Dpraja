package com.spdpboss.model;

import java.time.LocalDate;
import java.util.List;

public class ChartWeek {
    private LocalDate startDate;
    private String dateRangeStr;
    private List<GameHistory> records;

    public ChartWeek() {}

    public ChartWeek(LocalDate startDate, String dateRangeStr, List<GameHistory> records) {
        this.startDate = startDate;
        this.dateRangeStr = dateRangeStr;
        this.records = records;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getDateRangeStr() {
        return dateRangeStr;
    }

    public void setDateRangeStr(String dateRangeStr) {
        this.dateRangeStr = dateRangeStr;
    }

    public List<GameHistory> getRecords() {
        return records;
    }

    public void setRecords(List<GameHistory> records) {
        this.records = records;
    }
}
