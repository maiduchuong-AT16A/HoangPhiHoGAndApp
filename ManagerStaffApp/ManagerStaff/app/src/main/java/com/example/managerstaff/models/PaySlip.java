package com.example.managerstaff.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class PaySlip {
    @SerializedName("id_pay_slip")
    @Expose
    private int idPaySlip;
    @SerializedName("num_day_off")
    @Expose
    private int numDayOff;
    @SerializedName("price")
    @Expose
    private double price;
    @SerializedName("num_work_day")
    @Expose
    private int numWorkDay;
    @SerializedName("evaluate")
    @Expose
    private String evaluate;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("month")
    @Expose
    private int month;
    @SerializedName("num_late_day")
    @Expose
    private int numLateDay;
    @SerializedName("num_leave_early")
    @Expose
    private int numLeaveEarly;
    @SerializedName("year")
    @Expose
    private int year;
    @SerializedName("workdays")
    @Expose
    private ArrayList<Workday> listWorkdays;
    @SerializedName("salary_payments")
    @Expose
    private ArrayList<SalaryPayment> listSalaryPayments;
    @SerializedName("user")
    @Expose
    private User user;

    public PaySlip() {
    }

    public PaySlip(int idPaySlip, int numDayOff, double price, int numWorkDay, String evaluate, String note, int month, int numLateDay, int numLeaveEarly, int year, ArrayList<Workday> listWorkdays, ArrayList<SalaryPayment> listSalaryPayments, User user) {
        this.idPaySlip = idPaySlip;
        this.numDayOff = numDayOff;
        this.price = price;
        this.numWorkDay = numWorkDay;
        this.evaluate = evaluate;
        this.note = note;
        this.month = month;
        this.numLateDay = numLateDay;
        this.numLeaveEarly = numLeaveEarly;
        this.year = year;
        this.listWorkdays = listWorkdays;
        this.listSalaryPayments = listSalaryPayments;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getNumLeaveEarly() {
        return numLeaveEarly;
    }

    public void setNumLeaveEarly(int numLeaveEarly) {
        this.numLeaveEarly = numLeaveEarly;
    }

    public ArrayList<SalaryPayment> getListSalaryPayments() {
        return listSalaryPayments;
    }

    public void setListSalaryPayments(ArrayList<SalaryPayment> listSalaryPayments) {
        this.listSalaryPayments = listSalaryPayments;
    }

    public int getIdPaySlip() {
        return idPaySlip;
    }

    public void setIdPaySlip(int idPaySlip) {
        this.idPaySlip = idPaySlip;
    }

    public int getNumDayOff() {
        return numDayOff;
    }

    public void setNumDayOff(int numDayOff) {
        this.numDayOff = numDayOff;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getNumWorkDay() {
        return numWorkDay;
    }

    public void setNumWorkDay(int numWorkDay) {
        this.numWorkDay = numWorkDay;
    }

    public String getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(String evaluate) {
        this.evaluate = evaluate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getNumLateDay() {
        return numLateDay;
    }

    public void setNumLateDay(int numLateDay) {
        this.numLateDay = numLateDay;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public ArrayList<Workday> getListWorkdays() {
        return listWorkdays;
    }

    public void setListWorkdays(ArrayList<Workday> listWorkdays) {
        this.listWorkdays = listWorkdays;
    }
}
