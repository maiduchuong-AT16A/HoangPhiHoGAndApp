package com.example.managerstaff.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class SalaryPayment {
    @SerializedName("id_payment")
    @Expose
    private int idPayment;
    @SerializedName("total_wage")
    @Expose
    private String totalWage;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("type_payment")
    @Expose
    private String typePayment;

    public SalaryPayment() {
    }

    public SalaryPayment(int idPayment, String totalWage, String time, String content, String typePayment) {
        this.idPayment = idPayment;
        this.totalWage = totalWage;
        this.time = time;
        this.content = content;
        this.typePayment = typePayment;
    }

    public int getIdPayment() {
        return idPayment;
    }

    public void setIdPayment(int idPayment) {
        this.idPayment = idPayment;
    }

    public String getTotalWage() {
        return totalWage;
    }

    public void setTotalWage(String totalWage) {
        this.totalWage = totalWage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTypePayment() {
        return typePayment;
    }

    public void setTypePayment(String typePayment) {
        this.typePayment = typePayment;
    }
}
