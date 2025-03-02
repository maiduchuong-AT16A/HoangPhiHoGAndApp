package com.example.managerstaff.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class CheckOut {
    @SerializedName("id_check_out")
    @Expose
    private int idTimeOut;
    @SerializedName("time_out")
    @Expose
    private String timeOut;
    @SerializedName("money")
    @Expose
    private double money;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("time_difference")
    @Expose
    private String timeDifference;

    public CheckOut() {
    }

    public CheckOut(int idTimeOut, String timeOut) {
        this.idTimeOut = idTimeOut;
        this.timeOut = timeOut;
    }

    public CheckOut(int idTimeOut, String timeOut, double money, String status) {
        this.idTimeOut = idTimeOut;
        this.timeOut = timeOut;
        this.money = money;
        this.status = status;
    }

    public CheckOut(int idTimeOut, String timeOut, double money, String status, String timeDifference) {
        this.idTimeOut = idTimeOut;
        this.timeOut = timeOut;
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

    public int getIdTimeOut() {
        return idTimeOut;
    }

    public void setIdTimeOut(int idTimeOut) {
        this.idTimeOut = idTimeOut;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }
}
