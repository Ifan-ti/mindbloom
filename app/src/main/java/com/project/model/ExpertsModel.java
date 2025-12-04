package com.project.model;

import com.google.gson.annotations.SerializedName;

public class ExpertsModel {
    @SerializedName("id")
    private int id;

    @SerializedName("fullName")
    private String name;

    @SerializedName("expertise_area")
    private String expertise_area;

    @SerializedName("avatar")
    private String avatar;
    // ✅ TAMBAHKAN CONSTRUCTOR KOSONG (untuk Gson)
    public ExpertsModel() {
    }

    // ✅ TAMBAHKAN CONSTRUCTOR LENGKAP (untuk testing)
    public ExpertsModel(int id, String name, String expertise_area, String avatar) {
        this.avatar = avatar;
        this.id = id;
        this.name = name;
        this.expertise_area = expertise_area;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getExpertise_area() {
        return expertise_area;
    }

    public String getAvatar() {
        return avatar;
    }

    // ✅ TAMBAHKAN toString() untuk debugging
    @Override
    public String toString() {
        return "ExpertsModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", expertise_area='" + expertise_area + '\'' +
                '}';
    }
}