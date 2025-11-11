package com.project.data;

import com.google.gson.annotations.SerializedName;

public class userModel {

    @SerializedName("id")
    private String idUser;

    @SerializedName("username")
    private String username;

    public int getIdUser(){
        return Integer.parseInt(idUser);
    }

    public String getUsername(){
        return username;
    }
}