package com.example.honoursproject.Data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Manga")
public class Manga {
    private String manga_id;
    private String title;
    private String author_id;
    private String author;
    private String artist_id;
    private String artist;
    private String cover_id;

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public String getManga_id() {
        return manga_id;
    }
    public void setManga_id(String manga_id) {
        this.manga_id = manga_id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor_id() {
        return author_id;
    }
    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getArtist_id() {
        return artist_id;
    }
    public void setArtist_id(String artist_id) {
        this.artist_id = artist_id;
    }

    public String getArtist() {
        return artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getCover_id() {
        return cover_id;
    }
    public void setCover_id(String cover_id) {
        this.cover_id = cover_id;
    }
}
