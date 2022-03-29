package com.example.honoursproject.Data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

//database that stores all info on plants the user enters
@Database(entities = {Manga.class}, version = 2)
public abstract class MangaDatabase extends RoomDatabase{

    public abstract MangaDao mangaDao();

    private static MangaDatabase INSTANCE = null;

    public static MangaDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (MangaDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context, MangaDatabase.class, "manga_database")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
