package com.ntu.hero.sql

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ntu.hero.Hero
import com.ntu.hero.R
import com.ntu.hero.global.GlobalVars
import com.ntu.hero.global.MPreferences
import com.ntu.hero.sql.entity.*
import java.util.*

object DatabaseHelper {
    val TAG = "JAY"

    private var db: HeroDatabase?
    private var sdb: SupportSQLiteDatabase

    // ===== MESSAGE LABELS =====
    const val MSG_TABLE_NAME = "Message"
    const val MSG_COLUMN_JID = "MsgJid"
    const val MSG_COLUMN_ISSENDER = "IsSender"
    const val MSG_COLUMN_ID = "MsgUniqueId"
    const val MSG_COLUMN_DATA = "MsgData"
    const val MSG_COLUMN_PATH = "MsgMediaPath"
    const val MSG_COLUMN_MEDIA_INFO = "MsgMediaInfo"
    const val MSG_COLUMN_RES_ID = "MsgMediaResID"
    const val MSG_COLUMN_OFFLINE = "MsgOffline"
    const val MSG_COLUMN_WORK_ID = "MsgWorkID"
    const val MSG_COLUMN_FLAGGED = "MsgFlagDate"
    const val MSG_COLUMN_REPLY_DATA = "MsgReplyData"
    const val MSG_COLUMN_REPLY_ID = "MsgReplyUniqueId"

    init {
        db = Hero.database
        sdb = db!!.openHelper.writableDatabase
    }

    // TRY USE ROOM FOR ALL

    //======= UNCONVENTIONAL METHOD
    //===== UPDATE DB (SINGLE)
    //update db 1 column
    fun updateDB1Col(tableName: String, cv: ContentValues, col1Name: String, col1Value: String) {
        db!!.runInTransaction {
            sdb.update(
                tableName,
                SQLiteDatabase.CONFLICT_IGNORE,
                cv,
                String.format("%s = ?", col1Name),
                arrayOf(col1Value)
            )
        }
    }

    //update db 2 columns
    fun updateDB2Col(
        tableName: String,
        cv: ContentValues,
        col1Name: String,
        col1Value: String,
        col2Name: String,
        col2Value: String
    ) {
        db!!.runInTransaction {
            sdb.update(
                tableName,
                SQLiteDatabase.CONFLICT_IGNORE,
                cv,
                String.format("%s = ? AND %s = ?", col1Name, col2Name),
                arrayOf(col1Value, col2Value)
            )
        }
    }

    //===== UPDATE DB (BULK) in GeneralUpdateQuery

    //===== DELETE DB (SINGLE)
    // delete db 1 column
    fun deleteRDB1Col(tableName: String, col1Name: String, col1Value: String) {
        db!!.runInTransaction {
            sdb.delete(tableName, String.format("%s = ?", col1Name), arrayOf(col1Value))
        }
    }

    // delete db 2 columns
    fun deleteRDB2Col(
        tableName: String,
        col1Name: String,
        col1Value: String,
        col2Name: String,
        col2Value: String
    ) {
        db!!.runInTransaction {
            sdb.delete(
                tableName,
                String.format("%s = ? AND %s = ?", col1Name, col2Name),
                arrayOf(col1Value, col2Value)
            )
        }
    }

    //===== DELETE DB (BULK)
    // 1 column use query

    fun deleteRDB2ColBulk(
        tableName: String,
        col1Name: String,
        col1ValueList: List<String>,
        col2Name: String,
        col2ValueList: List<String>
    ) {
        col1ValueList.forEachIndexed { i, it ->
            // delete first few values first
            sdb.delete(
                tableName,
                String.format("%s = ? AND %s = ?", col1Name, col2Name),
                arrayOf(it, col2ValueList[i])
            )

            // last item run in transaction to update db
            if (i == col1ValueList.lastIndex) {
                db!!.runInTransaction {
                    sdb.delete(
                        tableName,
                        String.format("%s = ? AND %s = ?", col1Name, col2Name),
                        arrayOf(it, col2ValueList[i])
                    )
                }
            }
        }
    }

    //======== [END] basics ========//


    //======= DataCollection-Related =======//
    // get list of data from relevant tables for posting to server
    fun getDataList(dataType: String): List<*>? {
        when (dataType) {
            GlobalVars.DATA_TYPE_APP_USE -> {
                return db!!.selectQuery().getFullListAU() as List<*>
            }

            GlobalVars.DATA_TYPE_LOCATION -> {
                return db!!.selectQuery().getFullListGPS() as List<*>
            }

            GlobalVars.DATA_TYPE_KEYBOARD_USE -> {
                return db!!.selectQuery().getFullListKU() as List<*>
            }

            GlobalVars.DATA_TYPE_MOTION -> {
                return db!!.selectQuery().getFullListMotion() as List<*>
            }

            else -> {
                return null
            }
        }
    }

    // clear relevant dataCollection table on posting success
    fun clearDataTable(dataType: String) {
        when (dataType) {
            GlobalVars.DATA_TYPE_APP_USE -> {
                db!!.deleteQuery().clearAppUsage()
            }

            GlobalVars.DATA_TYPE_LOCATION -> {
                db!!.deleteQuery().clearGPSLocation()
            }

            GlobalVars.DATA_TYPE_KEYBOARD_USE -> {
                db!!.deleteQuery().clearKeyboardUsage()
            }

            GlobalVars.DATA_TYPE_MOTION -> {
                db!!.deleteQuery().clearMotionData()
            }

            else -> {
            }
        }
    }


    //=== AppUsage Table
    fun checkAndInsertAU(appUsage: AppUsage) {
        // check if got last row first
        val lastRow = getLastRowAU()

        if (lastRow != null) { // got last row, update timeEnd
            updateLastRowAU(lastRow)
        }

        // insert new row with timeStart
        insertNewRowAU(appUsage)
    }

    // get last row from du
    private fun getLastRowAU(): Int? {
        return db!!.selectQuery().getLastRowDU()
    }

    // update last row timeEnd
    private fun updateLastRowAU(lastRow: Int) {
        db!!.updateQuery().updateLastRowDU(lastRow = lastRow, timeEnd = System.currentTimeMillis())
    }

    private fun insertNewRowAU(item: AppUsage) {
        db!!.insertQuery().insertAUItem(item)
    }


    //=== GPSLocation Table
    fun insertGPS(item: GPSLocation) {
        db!!.insertQuery().insertGPSItem(item)
    }


    //=== KeyboardUsage Table
    fun insertKU(item: KeyboardUsage) {
        db!!.insertQuery().insertKUItem(item)
    }


    //=== MotionData Table
    fun insertMotion(item: MotionData) {
        db!!.insertQuery().insertMotion(item)
    }


    //=== ContactID Table
    fun getOrInsertContactID(phoneNumber: String): String {
        var contactID = db!!.selectQuery().getContactID(phoneNumber)

        // insert new row if contactID null (no phone number in sqlite yet)
        if (contactID == null) {
            contactID = UUID.randomUUID().toString()

            db!!.insertQuery()
                .insertContactID(ContactID(cUniqueID = contactID, cPhoneNumber = phoneNumber))
        }

        return contactID
    }


    //======= HomeActivityList =======//
    // temp - populate home activity table
    fun populateHomeActiList(context: Context) {
        val homeActiList = listOf<HomeActivityList>(
            HomeActivityList(
                homeTitle = context.getString(R.string.home_title_1),
                homeDesc = context.getString(R.string.home_desc_1)
            ),
            HomeActivityList(
                homeTitle = context.getString(R.string.home_title_2),
                homeDesc = context.getString(R.string.home_desc_2)
            ),
            HomeActivityList(
                homeTitle = context.getString(R.string.home_title_3),
                homeDesc = context.getString(R.string.home_desc_3)
            )
        )

        db!!.insertQuery().insertBulkHomeActi(homeActiList)

        MPreferences.saveint("homeActiAdded", 1)
    }
}
