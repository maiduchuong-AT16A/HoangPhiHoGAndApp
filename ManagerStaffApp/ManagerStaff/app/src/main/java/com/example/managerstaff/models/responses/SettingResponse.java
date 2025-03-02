package com.example.managerstaff.models.responses;

import com.example.managerstaff.models.Setting;
import com.example.managerstaff.models.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class SettingResponse {
    @SerializedName("data")
    @Expose
    private Setting setting;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private int code;

    public SettingResponse() {
    }

    public SettingResponse(Setting setting, String message, int code) {
        this.setting = setting;
        this.message = message;
        this.code = code;
    }

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
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
