package com.sp.learntogether.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracking")
data class Track(
    @PrimaryKey val meetupUid: String,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP") val timestmp: Long,
    @ColumnInfo val lat: Double,
    @ColumnInfo val lng: Double
)