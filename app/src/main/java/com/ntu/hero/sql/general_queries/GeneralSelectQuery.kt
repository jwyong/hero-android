package com.ntu.hero.sql.general_queries

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.ntu.hero.sql.entity.*

/* Created by jay on 01/10/2018. */

@Dao
interface GeneralSelectQuery {
    @Query("SELECT * FROM HomeActivityList")
    fun getHomeActiList(): LiveData<List<HomeActivityList>>


    //===== Appointment
    @Query("SELECT * FROM Appointment ORDER BY apptDateTime")
    fun getApptMainList(): LiveData<List<Appointment>>


    //===== AppUsage
    @Query("SELECT MAX(`row`) FROM AppUsage")
    fun getLastRowDU(): Int?

    @Query("SELECT * FROM AppUsage")
    fun getFullListAU(): List<AppUsage>?


    //===== GPSLocation
    @Query("SELECT * FROM GPSLocation")
    fun getFullListGPS(): List<GPSLocation>?


    //===== MotionData
    @Query("SELECT * FROM MotionData")
    fun getFullListMotion(): List<MotionData>?


    //===== KeyboardUsage
    @Query("SELECT * FROM KeyboardUsage")
    fun getFullListKU(): List<KeyboardUsage>?


    //===== ContactID
    @Query("SELECT cUniqueID FROM ContactID WHERE cPhoneNumber = :phoneNumber")
    fun getContactID(phoneNumber: String): String?
}