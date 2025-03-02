package com.example.managerstaff.models.responses;

import com.example.managerstaff.models.StaffFeedBack;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ListFeedbackResponse {

    @SerializedName("data")
    @Expose
    private List<StaffFeedBack> listFeedbacks;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private int code;

    public ListFeedbackResponse() {
    }

    public ListFeedbackResponse(List<StaffFeedBack> listFeedbacks, String message, int code) {
        this.listFeedbacks = listFeedbacks;
        this.message = message;
        this.code = code;
    }

    public List<StaffFeedBack> getListFeedbacks() {
        return listFeedbacks;
    }

    public void setListFeedbacks(List<StaffFeedBack> listFeedbacks) {
        this.listFeedbacks = listFeedbacks;
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
