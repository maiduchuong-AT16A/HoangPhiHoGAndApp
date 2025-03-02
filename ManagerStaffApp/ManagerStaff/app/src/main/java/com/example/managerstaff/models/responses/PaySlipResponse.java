package com.example.managerstaff.models.responses;

import com.example.managerstaff.models.PaySlip;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class PaySlipResponse {

    @SerializedName("data")
    @Expose
    private PaySlip paySlip;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private int code;

    public PaySlipResponse() {
    }

    public PaySlipResponse(PaySlip paySlip, String message, int code) {
        this.paySlip = paySlip;
        this.message = message;
        this.code = code;
    }

    public PaySlip getPaySlip() {
        return paySlip;
    }

    public void setPaySlip(PaySlip paySlip) {
        this.paySlip = paySlip;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
