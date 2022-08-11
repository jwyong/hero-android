package com.ntu.hero.work_manager.gps_motion

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import com.ntu.hero.global.GlobalVars


class MotionHelper {
    fun requestActivityTransitionUpdates(context: Context) {
        // setup pending intent for motion service
        val intent = Intent(context, MotionTransitionIS::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // register broadcast receiver
//        LocalBroadcastManager.getInstance(context)
//            .registerReceiver(MotionTransitionIS(), IntentFilter("TEST"))

        val client = ActivityRecognition.getClient(context)

        // remove any updates first
        client.removeActivityTransitionUpdates(pendingIntent)
        client.removeActivityUpdates(pendingIntent)

        // setup activity updates
        val task = client.requestActivityUpdates(0, pendingIntent)

        // setup activity transition updates
//        val request = buildTransitionRequest()
//        val task = client.requestActivityTransitionUpdates(request, pendingIntent)
        task.addOnSuccessListener {
            Log.d(
                GlobalVars.TAG1,
                "MotionHelper: requestActivityTransitionUpdates success"
            )
        }
        task.addOnFailureListener {
            Log.d(GlobalVars.TAG1, "MotionHelper: requestActivityTransitionUpdates failure e = $it")
        }

    }

    private fun buildTransitionRequest(): ActivityTransitionRequest {
        val actiTypeList = listOf(
            DetectedActivity.STILL,
            DetectedActivity.WALKING,
            DetectedActivity.RUNNING,
            DetectedActivity.ON_BICYCLE,
            DetectedActivity.IN_VEHICLE
        )

        val transitions = ArrayList<ActivityTransition>()
        actiTypeList.forEach {
            transitions.add(
                ActivityTransition.Builder()
                    .setActivityType(it)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                    .build()
            )
            transitions.add(
                ActivityTransition.Builder()
                    .setActivityType(it)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                    .build()
            )
        }

        return ActivityTransitionRequest(transitions)
    }


}