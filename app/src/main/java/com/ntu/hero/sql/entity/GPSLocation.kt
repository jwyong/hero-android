package com.ntu.hero.sql.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class GPSLocation(
    @ColumnInfo(name = "timestamp") var gpsTimestamp: Long = 0,
    @ColumnInfo(name = "lat") var gpsLat: String? = null, // latitude
    @ColumnInfo(name = "lng") var gpsLng: String? = null // longitude
) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "row") var gpsRow: Int? = null
}
