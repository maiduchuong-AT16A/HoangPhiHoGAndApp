package com.example.managerstaff.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Part {
    @SerializedName("id_part")
    @Expose
    private int idPart;
    @SerializedName("name_part")
    @Expose
    private String namePart;
    @SerializedName("describe_part")
    @Expose
    private String describePart;

    public Part() {
    }

    public Part(int idPart, String namePart, String describePart) {
        this.idPart = idPart;
        this.namePart = namePart;
        this.describePart = describePart;
    }

    public int getIdPart() {
        return idPart;
    }

    public void setIdPart(int idPart) {
        this.idPart = idPart;
    }

    public String getNamePart() {
        return namePart;
    }

    public void setNamePart(String namePart) {
        this.namePart = namePart;
    }

    public String getDescribePart() {
        return describePart;
    }

    public void setDescribePart(String describePart) {
        this.describePart = describePart;
    }
}
