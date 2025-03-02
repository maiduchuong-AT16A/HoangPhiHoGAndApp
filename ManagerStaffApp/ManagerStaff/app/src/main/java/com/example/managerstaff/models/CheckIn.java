package com.example.managerstaff.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class CheckIn {
    @SerializedName("id_check_in")
    @Expose
    private int idTimeIn;
    @SerializedName("time_in")
    @Expose
    private String timeIn;
    @SerializedName("money")
    @Expose
    private double money;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("time_difference")
    @Expose
    private String timeDifference;

    public CheckIn() {
    }

    public CheckIn(int idTimeIn, String timeIn) {
        this.idTimeIn = idTimeIn;
        this.timeIn = timeIn;
    }

    public CheckIn(int idTimeIn, String timeIn, double money, String status) {
        this.idTimeIn = idTimeIn;
        this.timeIn = timeIn;
        this.money = money;
        this.status = status;
    }

    public CheckIn(int idTimeIn, String timeIn, double money, String status, String timeDifference) {
        this.idTimeIn = idTimeIn;
        this.timeIn = timeIn;
        this.money = money;
        this.status = status;
        this.timeDifference = timeDifference;
    }

    public String getTimeDifference() {
        return timeDifference;
    }

    public void setTimeDifference(String timeDifference) {
        this.timeDifference = timeDifference;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getIdTimeIn() {
        return idTimeIn;
    }

    public void setIdTimeIn(int idTimeIn) {
        this.idTimeIn = idTimeIn;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(String timeIn) {
        this.timeIn = timeIn;
    }
}
