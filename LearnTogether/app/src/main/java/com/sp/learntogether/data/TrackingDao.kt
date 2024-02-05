package com.sp.learntogether.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.sp.learntogether.models.Track


@Dao
interface TrackingDao {
    @Query("SELECT * FROM tracking")
    fun getAll(): List<Track>
    @Insert
    fun insertOne(track: Track)

    @Delete
    fun deleteOne(track: Track)

    @Query("SELECT * FROM tracking WHERE timestmp >= :current")
    fun getFuture(current: Long): List<Track>

}
