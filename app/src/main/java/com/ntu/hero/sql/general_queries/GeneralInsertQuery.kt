package sg.com.agentapp.sql.general_queries

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.ntu.hero.sql.entity.*

/* Created by jay on 01/10/2018. */

@Dao
interface GeneralInsertQuery {
//    @Insert
//    fun insertCR(contactRoster: ContactRoster): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBulkHomeActi(homeActiList: List<HomeActivityList>)


    //===== APPOINTMENT
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertApptItem(apptItem: Appointment)


    //===== AppUsage
    @Insert
    fun insertAUItem(item: AppUsage)


    //===== GPSLocation
    @Insert
    fun insertGPSItem(item: GPSLocation)


    //===== MotionData
    @Insert
    fun insertMotion(item: MotionData)


    //===== KeyboardUsage
    @Insert
    fun insertKUItem(item: KeyboardUsage)


    //===== ContactID
    @Insert
    fun insertContactID(item: ContactID)
}
