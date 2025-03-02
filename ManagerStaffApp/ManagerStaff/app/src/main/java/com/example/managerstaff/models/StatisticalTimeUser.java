package com.example.managerstaff.models;

public class StatisticalTimeUser {

    private String dayOfWeek;
    private String dayOfWeekName;
    private boolean isDayOff;
    private double wage;



    public StatisticalTimeUser() {
        this.isDayOff=true;
    }

    public StatisticalTimeUser(String dayOfWeek, String dayOfWeekName, boolean isDayOff, double wage) {
        this.dayOfWeek = dayOfWeek;
        this.dayOfWeekName = dayOfWeekName;
        this.isDayOff = isDayOff;
        this.wage=wage;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getDayOfWeekName() {
        return dayOfWeekName;
    }

    public void setDayOfWeekName(String dayOfWeekName) {
        this.dayOfWeekName = dayOfWeekName;
    }

    public boolean isDayOff() {
        return isDayOff;
    }

    public void setDayOff(boolean dayOff) {
        isDayOff = dayOff;
    }

    public double getWage() {
        return wage;
    }

    public void setWage(double wage) {
        this.wage = wage;
    }
}
