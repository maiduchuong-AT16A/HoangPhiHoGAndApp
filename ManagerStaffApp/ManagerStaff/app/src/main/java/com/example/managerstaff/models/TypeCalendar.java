package com.example.managerstaff.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class TypeCalendar {

    @SerializedName("id_type")
    @Expose
    private int idType;
    @SerializedName("type_name")
    @Expose
    private String typeName;
    @SerializedName("describe")
    @Expose
    private String describe;

    public TypeCalendar() {
    }

    public TypeCalendar(int idType, String typeName, String describe) {
        this.idType = idType;
        this.typeName = typeName;
        this.describe = describe;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
