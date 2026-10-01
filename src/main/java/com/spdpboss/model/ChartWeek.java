package com.spdpboss.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ChartWeek {
    private LocalDate startDate;
    private String dateRangeStr;
    private List<DayCell> days = new ArrayList<>();

    public ChartWeek() {}

    public ChartWeek(LocalDate startDate, String dateRangeStr, List<DayCell> days) {
        this.startDate = startDate;
        this.dateRangeStr = dateRangeStr;
        this.days = days != null ? days : new ArrayList<>();
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

    public List<DayCell> getDays() {
        return days;
    }

    public void setDays(List<DayCell> days) {
        this.days = days;
    }
}
