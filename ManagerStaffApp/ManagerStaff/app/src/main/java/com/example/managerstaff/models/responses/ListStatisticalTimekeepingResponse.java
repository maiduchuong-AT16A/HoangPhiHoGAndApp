package com.example.managerstaff.models.responses;

import com.example.managerstaff.models.StatisticalTimeKeeping;
import com.example.managerstaff.models.TypePost;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ListStatisticalTimekeepingResponse {
    @SerializedName("data")
    @Expose
    private List<StatisticalTimeKeeping> listStatistic;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private int code;

    public ListStatisticalTimekeepingResponse() {
    }

    public ListStatisticalTimekeepingResponse(List<StatisticalTimeKeeping> listStatistic, String message, int code) {
        this.listStatistic = listStatistic;
        this.message = message;
        this.code = code;
    }

    public List<StatisticalTimeKeeping> getListStatistic() {
        return listStatistic;
    }

    public void setListStatistic(List<StatisticalTimeKeeping> listStatistic) {
        this.listStatistic = listStatistic;
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
