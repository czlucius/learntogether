package com.sp.learntogether.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sp.learntogether.models.Book;

import java.util.List;

@Dao
public interface BookDao {
    @Query("SELECT * FROM book")
    List<Book> getAll();

    @Query("SELECT * FROM book")
    LiveData<List<Book>> getAllLive();


    @Insert(onConflict = OnConflictStrategy.IGNORE )
    void insertOne(Book book);

    @Delete
    void deleteOne(Book book);


}
