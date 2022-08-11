package com.ntu.hero.sql

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ntu.hero.global.GlobalVars
import com.ntu.hero.sql.entity.*
import com.ntu.hero.sql.general_queries.GeneralSelectQuery
import sg.com.agentapp.sql.general_queries.GeneralDeleteQuery
import sg.com.agentapp.sql.general_queries.GeneralInsertQuery
import sg.com.agentapp.sql.general_queries.GeneralUpdateQuery

//tables
//IMPORTANT - remember to reset version before going LIVE
@Database(
    entities = [HomeActivityList::class, Appointment::class, AppUsage::class, GPSLocation::class, KeyboardUsage::class, MotionData::class, ContactID::class],
    version = 7,
    exportSchema = false
)
abstract class HeroDatabase : RoomDatabase() {
    abstract fun selectQuery(): GeneralSelectQuery
    abstract fun updateQuery(): GeneralUpdateQuery
    abstract fun insertQuery(): GeneralInsertQuery
    abstract fun deleteQuery(): GeneralDeleteQuery

    companion object {
        @Volatile
        private var INSTANCE: HeroDatabase? = null

        @Synchronized
        fun getInstance(context: Context): HeroDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    HeroDatabase::class.java,
                    GlobalVars.DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }

            return INSTANCE
        }
    }
}
