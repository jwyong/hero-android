package sg.com.agentapp.sql.general_queries

import androidx.room.Dao
import androidx.room.Query

/* Created by jay on 01/10/2018. */

@Dao
interface GeneralDeleteQuery {
    //===== DataCollection-Related =====
    //=== AppUsage
    @Query("DELETE FROM AppUsage")
    fun clearAppUsage()


    //=== GPSLocation
    @Query("DELETE FROM GPSLocation")
    fun clearGPSLocation()


    //=== MotionData
    @Query("DELETE FROM MotionData")
    fun clearMotionData()


    //=== KeyboardUsage
    @Query("DELETE FROM KeyboardUsage")
    fun clearKeyboardUsage()
}
