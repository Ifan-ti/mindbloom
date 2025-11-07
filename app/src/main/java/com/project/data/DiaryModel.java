package com.project.data;

import com.google.gson.annotations.SerializedName;

public class DiaryModel {
    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String Content;

    @SerializedName("name_tag")
    private String nameTag;

    @SerializedName("entry_date")
    private String entryDate;
    @SerializedName("cover_image_url")
    private String coverImageUrl;

    @SerializedName("id")
    private String id_diary;
    @SerializedName("author")
    private String author;







    public String getTitle() {
        return title;
    }


    public String getContent() {
        return Content;
    }




    public String getEntryDate() {
        return entryDate;
    }
    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public String getIdDiary(){
        return id_diary;
    }

    public String getAuthor(){
        return author;
    }








}
