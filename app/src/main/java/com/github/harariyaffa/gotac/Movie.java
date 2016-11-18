package com.github.harariyaffa.gotac;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Movie implements Serializable {

    private int _id;
    private String title;
    private String description;
    private String genre;
    private String imgUrl;


    ///constructor for manual movie
    public Movie(String title, String description, String genre, String url){
        //this._id=_id;
        this.title=title;
        this.genre = genre;
        this.description=description;
        this.imgUrl=url;
    }
    public Movie(JSONObject jsonObject) {
        try {
            this.title = jsonObject.getString("Title");
            this.genre = jsonObject.getString("Genre");
            this.description = jsonObject.getString("Plot");
            this.imgUrl = jsonObject.getString("Poster");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //========get & set==========

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    //=================


    @Override
    public String toString() {
        return "Movie{" +
                "_id=" + _id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", genre='" + genre + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}