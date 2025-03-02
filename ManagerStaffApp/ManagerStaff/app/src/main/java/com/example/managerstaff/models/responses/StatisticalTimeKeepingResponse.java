package com.example.managerstaff.models.responses;

import com.example.managerstaff.models.Setting;
import com.example.managerstaff.models.StatisticalTimeKeeping;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class StatisticalTimeKeepingResponse {
    @SerializedName("data")
    @Expose
    private StatisticalTimeKeeping statisticalTimeKeeping;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private int code;

    public StatisticalTimeKeepingResponse() {
    }

    public StatisticalTimeKeepingResponse(StatisticalTimeKeeping statisticalTimeKeeping, String message, int code) {
        this.statisticalTimeKeeping = statisticalTimeKeeping;
        this.message = message;
        this.code = code;
    }

    public StatisticalTimeKeeping getStatisticalTimeKeeping() {
        return statisticalTimeKeeping;
    }

    public void setStatisticalTimeKeeping(StatisticalTimeKeeping statisticalTimeKeeping) {
        this.statisticalTimeKeeping = statisticalTimeKeeping;
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
