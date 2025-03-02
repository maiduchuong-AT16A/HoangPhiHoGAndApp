package com.example.managerstaff.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class User {
    @SerializedName("id_user")
    @Expose
    private int idUser;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("is_admin")
    @Expose
    private int isAdmin;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("wage")
    @Expose
    private double wage;
    @SerializedName("notifications")
    @Expose
    private ArrayList<NotificationData> listNotifications;
    @SerializedName("account")
    @Expose
    private Account account;
    @SerializedName("part")
    @Expose
    private Part part;
    @SerializedName("position")
    @Expose
    private Position position;

    public User() {
    }

    public User(int idUser, String avatar, String fullName, String birthday, String gender, String address, String email, int isAdmin, String phone, double wage, ArrayList<NotificationData> listNotifications, Account account, Part part, Position position) {
        this.idUser = idUser;
        this.avatar = avatar;
        this.fullName = fullName;
        this.birthday = birthday;
        this.gender = gender;
        this.address = address;
        this.email = email;
        this.isAdmin = isAdmin;
        this.phone = phone;
        this.wage = wage;
        this.listNotifications = listNotifications;
        this.account = account;
        this.part = part;
        this.position = position;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getWage() {
        return wage;
    }

    public void setWage(double wage) {
        this.wage = wage;
    }

    public ArrayList<NotificationData> getListNotifications() {
        return listNotifications;
    }

    public void setListNotifications(ArrayList<NotificationData> listNotifications) {
        this.listNotifications = listNotifications;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
