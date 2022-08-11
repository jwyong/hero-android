package com.ntu.hero.work_manager.gps_motion

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ntu.hero.global.GlobalVars


// post to server once daily
class GPSMotionWorker(private val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private val gpsHelper = GPSHelper(context)

    override fun doWork(): Result {
        Log.d(GlobalVars.TAG1, "GPSMotionWorker: doWork ")

        //=== GPS
        if (gpsHelper.checkGPSPerms()) {
            // get current location from gps/network
            gpsHelper.getCurrentLocation()
        }

        //=== MOTION
        MotionHelper().requestActivityTransitionUpdates(context)

        return Result.success()
    }
}