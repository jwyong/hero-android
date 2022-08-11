package com.ntu.hero.work_manager.gps_motion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityRecognitionResult
import com.ntu.hero.global.GlobalVars
import com.ntu.hero.sql.DatabaseHelper
import com.ntu.hero.sql.entity.MotionData


class MotionTransitionIS : BroadcastReceiver() {
    private var lastActi: ActivityRecognitionResult? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        val result = ActivityRecognitionResult.extractResult(intent)

//        val detectedActivities = result.probableActivities as ArrayList
//        detectedActivities.forEach {
//            Log.d(GlobalVars.TAG1, "MotionTransitionIS: onHandleWork acti = $it")
//        }

        // insert most probable acti into sqlite ONLY IF different from last item
        val mostProbActi = result.mostProbableActivity
        if (lastActi == null || !lastActi!!.equals(mostProbActi)) { // last acti null or last acti not equal only insert
            // get stringID from type int
            val strName = "md_${mostProbActi.type}"
            val strID = context?.resources?.getIdentifier(strName, "string", context.packageName)
            val typeStr = context?.getString(strID!!)

            Log.d(GlobalVars.TAG1, "MotionTransitionIS: onReceive type = $typeStr")

            DatabaseHelper.insertMotion(MotionData(mdMotionType = typeStr, mdTimestamp = System.currentTimeMillis()))
        }


//        if (ActivityTransitionResult.hasResult(intent)) {
//            val result2 = ActivityTransitionResult.extractResult(intent)
//            result2?.run {
//                for (event in transitionEvents) {
//                    event?.apply {
//                        // Handle chronological sequence of events....
//                        Log.d(GlobalVars.TAG1, "MotionTransitionIS: onHandleWork = $this")
//                    }
//                }
//            }
//        }
    }

}