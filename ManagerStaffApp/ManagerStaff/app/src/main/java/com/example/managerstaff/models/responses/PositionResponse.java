package com.example.managerstaff.models.responses;

import com.example.managerstaff.models.Part;
import com.example.managerstaff.models.Position;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class PositionResponse {

    @SerializedName("data")
    @Expose
    private List<Position> listPositions;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private int code;

    public PositionResponse() {
    }

    public PositionResponse(List<Position> listPositions, String message, int code) {
        this.listPositions = listPositions;
        this.message = message;
        this.code = code;
    }

    public List<Position> getListPositions() {
        return listPositions;
    }

    public void setListPositions(List<Position> listPositions) {
        this.listPositions = listPositions;
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
