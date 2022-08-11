package com.ntu.hero.sql.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class MotionData(
    var mdMotionType: String? = null, // e.g. "STILL"
    var mdTimestamp: Long = 0 // timestamp of current motion
) {

    @PrimaryKey(autoGenerate = true)
    var mdRow: Int? = null
}
