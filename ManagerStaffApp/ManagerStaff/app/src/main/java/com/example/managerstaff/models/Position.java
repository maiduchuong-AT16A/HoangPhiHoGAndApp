package com.example.managerstaff.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Position {
    @SerializedName("id_position")
    @Expose
    private int idPosition;
    @SerializedName("name_position")
    @Expose
    private String namePosition;
    @SerializedName("describe_position")
    @Expose
    private String describePosition;

    public Position() {
    }

    public Position(int idPosition, String namePosition, String describePosition) {
        this.idPosition = idPosition;
        this.namePosition = namePosition;
        this.describePosition = describePosition;
    }

    public int getIdPosition() {
        return idPosition;
    }

    public void setIdPosition(int idPosition) {
        this.idPosition = idPosition;
    }

    public String getNamePosition() {
        return namePosition;
    }

    public void setNamePosition(String namePosition) {
        this.namePosition = namePosition;
    }

    public String getDescribePosition() {
        return describePosition;
    }

    public void setDescribePosition(String describePosition) {
        this.describePosition = describePosition;
    }
}
