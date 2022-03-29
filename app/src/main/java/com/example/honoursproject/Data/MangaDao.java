package com.example.honoursproject.Data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

//various methods to get/set data from database
@Dao
public interface MangaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Manga manga);

    @Update
    public void update(Manga manga);

    @Delete
    public void delete(Manga manga);

    @Query("SELECT * from Manga")
    public List<Manga> getAllMangas();

    @Query("SELECT * from Manga WHERE id LIKE :id")
    public Manga getManga(int id);

    @Query("SELECT * from Manga WHERE manga_id LIKE :manga_id")
    public Manga getManga(String manga_id);
}
