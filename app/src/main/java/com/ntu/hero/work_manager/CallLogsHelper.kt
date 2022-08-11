package com.ntu.hero.work_manager

import android.content.Context
import android.provider.CallLog.Calls
import android.util.Log
import com.ntu.hero.global.GlobalVars
import com.ntu.hero.global.MPreferences
import com.ntu.hero.global.PermissionHelper
import com.ntu.hero.sql.DatabaseHelper
import com.ntu.hero.sql.entity.CallLogTable


class CallLogsHelper {
    private val permissionHelper = PermissionHelper()

    fun getCallLogsList(context: Context): MutableList<CallLogTable>? {
        // check if got read logs perms
        val permStrArr = arrayOf(
            // Call Logs
            android.Manifest.permission.READ_CALL_LOG
        )

        var list: MutableList<CallLogTable>? = null
        if (permissionHelper.hasPermissions(context, *permStrArr)) {
            Log.d(GlobalVars.TAG1, "CallLogsHelper: getCallLogsList got perms")

            // got perms - get timestamp of last sync
            val lastTimeStamp = MPreferences.getValue(GlobalVars.PREF_CALL_LOGS_LAST_TIME) ?: "0"

            // get call logs where timestamp >= to last sync timestamp
            list = getCallDetails(context, lastTimeStamp)

        } else {
            Log.d(GlobalVars.TAG1, "CallLogsHelper: getCallLogsList no perms")
        }

        return list

    }

    // get list from phone call logs
    private fun getCallDetails(context: Context, lastTimeStamp: String): MutableList<CallLogTable>? {
        Log.d(GlobalVars.TAG1, "CallLogsHelper: getCallDetails lastTimeStamp = $lastTimeStamp")

        var list: MutableList<CallLogTable>? = null

        val projection = arrayOf(Calls.NUMBER, Calls.TYPE, Calls.DATE, Calls.DURATION)
        val selection = Calls.DATE + " > ?"
        val selArgs = arrayOf(lastTimeStamp)
        val managedCursor = context.contentResolver.query(
            Calls.CONTENT_URI,
            projection,
            selection,
            selArgs,
            Calls.DATE + " DESC"
        )

        if (managedCursor != null) { // no logs
            Log.d(GlobalVars.TAG1, "CallLogsHelper: getCallDetails cursor size = ${managedCursor.count}")

            list = mutableListOf()

            val number = managedCursor.getColumnIndex(Calls.NUMBER)
            val type = managedCursor.getColumnIndex(Calls.TYPE)
            val date = managedCursor.getColumnIndex(Calls.DATE)
            val duration = managedCursor.getColumnIndex(Calls.DURATION)

            while (managedCursor.moveToNext()) {
                val phoneNumber = managedCursor.getString(number)
                val callType = managedCursor.getString(type)
                val timeStamp = managedCursor.getString(date)
                val callDuration = managedCursor.getString(duration)

                // get unique contactID from contactID table
                val contactID = DatabaseHelper.getOrInsertContactID(phoneNumber)

                // add to list
                list.add(
                    CallLogTable(
                        contactID = contactID,
                        callType = callType,
                        callTimestamp = timeStamp,
                        callDuration = callDuration
                    )
                )

                Log.d(
                    GlobalVars.TAG1,
                    "CallLogsHelper: getCallDetails phone = $phoneNumber, type = $callType, timeStamp = $timeStamp, duration = $callDuration"
                )
            }
            managedCursor.close()
        } else {
            Log.d(GlobalVars.TAG1, "CallLogsHelper: getCallDetails cursor null")
        }

        return list
    }
}