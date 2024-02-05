package com.sp.learntogether.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.sp.learntogether.models.Book;
import com.sp.learntogether.models.Track;

@Database(entities = {Book.class, Track.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BookDao bookDao();

    public abstract TrackingDao trackingDao();

    private static AppDatabase INSTANCE;
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "lt-app-db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
