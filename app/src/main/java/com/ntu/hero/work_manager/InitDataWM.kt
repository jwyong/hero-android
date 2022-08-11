package com.ntu.hero.work_manager

import android.content.Context
import android.util.Log
import androidx.work.*
import com.ntu.hero.global.GlobalVars
import com.ntu.hero.work_manager.gps_motion.GPSMotionWorker
import java.util.concurrent.TimeUnit

class InitDataWM(private val context: Context) {
    fun initDataWM() {
        Log.d(GlobalVars.TAG1, "InitDataWM: initDataWM ")

        // TEMP - clear all work for testing
//        WorkManager.getInstance(context).cancelAllWork()

        // schedule gps/accel data collection (once every 30 mins)
        setGPSMotionPeriodic()

        // schedule data uploading for ALL data types (once daily)
        setUploadDataPeriodic()
    }

    // set periodic data uploading worker
    private fun setUploadDataPeriodic() {
        Log.d(GlobalVars.TAG1, "InitDataWM: setUploadDataPeriodic ")

        // set frequence for uploading (e.g. once every 1 day)
        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            DataUploadWorker::class.java, 1, TimeUnit.DAYS
        )
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .setInputData(
                Data.Builder().putStringArray(
                    "dataTypes",
                    arrayOf(
                        GlobalVars.DATA_TYPE_APP_USE,
                        GlobalVars.DATA_TYPE_LOCATION,
                        GlobalVars.DATA_TYPE_KEYBOARD_USE,
                        GlobalVars.DATA_TYPE_MOTION,
                        GlobalVars.DATA_TYPE_CALL_LOGS
                    )
                ).build()
            )
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            GlobalVars.WM_UPLOAD,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }

    // set periodic motion and gps related data collection
    private fun setGPSMotionPeriodic() {
        Log.d(GlobalVars.TAG1, "InitDataWM: setGPSMotionPeriodic ")

        // set frequency (e.g. once every 15 mins)
        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            GPSMotionWorker::class.java, 15, TimeUnit.MINUTES
        )
            .setConstraints(Constraints.Builder().setRequiresBatteryNotLow(true).build())
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            GlobalVars.WM_GPS_MOTION,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }
}