package com.ntu.hero.sql.entity

import android.text.format.DateFormat
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ntu.hero.global.GlobalVars
import java.util.*

@Entity
class Appointment(
    var apptID: String? = null,
    var apptTitle: String? = null, // e.g. "Doctor Appointment"
    var apptTimeStamp: Long = 0, // appt creation timestamp
    var apptDateTime: Long = 0, // appt timestamp - for reminder
    var apptTimeRangeStr: String? = null, // appt timestamp - for reminder
    var apptDocName: String? = null,
    var apptDocImg: String? = null,

    var apptSrvName: String? = null, // e.g. "NTU Clinic"
    var apptAddress: String? = null,
    var apptLat: String? = null,
    var apptLng: String? = null

) {

    @PrimaryKey(autoGenerate = true)
    var apptRow: Int? = null

    fun getFormattedDateTime(l: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = l

        return DateFormat.format("MMM d, yyyy h:mmA", cal).toString()
    }

    fun getFormattedDateOnly(l: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = l

        return DateFormat.format("EEE, MMM d, yyyy", cal).toString()
    }

    fun getMapImgUrl(): String {
        return "https://maps.googleapis.com/maps/api/staticmap?size=600x250&markers=color:red%7C$apptLat,$apptLng&key=${GlobalVars.G_MAPS_API_KEY}"
    }
}
