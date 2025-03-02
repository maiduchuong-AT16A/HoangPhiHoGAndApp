package com.example.managerstaff.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Account {
    @SerializedName("id")
    @Expose
    private int idAccount;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("key")
    @Expose
    private String key;

    public Account() {
    }

    public Account(int idAccount, String username, String password) {
        this.idAccount = idAccount;
        this.username = username;
        this.password = password;
    }

    public Account(int idAccount, String username, String password, String code, String key) {
        this.idAccount = idAccount;
        this.username = username;
        this.password = password;
        this.code = code;
        this.key = key;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getIdAccount() {
        return idAccount;
    }

    public void setIdAccount(int idAccount) {
        this.idAccount = idAccount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
