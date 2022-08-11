package com.ntu.hero.work_manager.gps_motion

import android.app.Activity
import android.content.Context
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.ntu.hero.global.GlobalVars
import com.ntu.hero.global.PermissionHelper
import com.ntu.hero.sql.DatabaseHelper
import com.ntu.hero.sql.entity.GPSLocation

class GPSHelper(private val ctx: Context) {
    private val permissionHelper = PermissionHelper()
    private val PERMS_REQ_CODE = 1

    //=== GPS
    fun checkGPSPerms(): Boolean {
        val permStrArr = arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (!permissionHelper.hasPermissions(ctx, *permStrArr)) { // no perm
            // straight request permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(ctx as Activity, permStrArr, PERMS_REQ_CODE)
            }

            Log.d(GlobalVars.TAG1, "GPSMotionWorker: checkGPSPerms no perms")

            return false

        } else {
            Log.d(GlobalVars.TAG1, "GPSMotionWorker: checkGPSPerms got perms")

            return true
        }
    }

    fun getCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                Log.d(GlobalVars.TAG1, "GPSMotionWorker: getCurrentLocation lastLocation = $location")

                // check if location is null
                if (location != null) { // not null, straight insert to sqlite
                    DatabaseHelper.insertGPS(
                        GPSLocation(
                            gpsTimestamp = System.currentTimeMillis(),
                            gpsLat = location.latitude.toString(),
                            gpsLng = location.longitude.toString()
                        )
                    )
                } else { // no location, try to get location

                    // create location request
                    val locationRequest = LocationRequest.create()
                    locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    locationRequest.interval = 10 * 1000

                    // create callback
                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult?) {
                            Log.d(GlobalVars.TAG1, "GPSMotionWorker: onLocationResult currentLocation = $locationResult")

                            if (locationResult == null) {
                                return
                            }
                            for (loc in locationResult.locations) {
                                if (loc != null) {
                                    // insert to sqlite
                                    DatabaseHelper.insertGPS(
                                        GPSLocation(
                                            gpsTimestamp = System.currentTimeMillis(),
                                            gpsLat = loc.latitude.toString(),
                                            gpsLng = loc.longitude.toString()
                                        )
                                    )

                                    // remove updates
                                    fusedLocationClient.removeLocationUpdates(this)
                                }
                            }
                        }
                    }
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        null
                    )
                }
            }
    }
}