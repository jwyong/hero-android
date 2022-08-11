package com.ntu.hero.sql.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class AppUsage(
    @ColumnInfo(name = "package_name") var auPackageName: String? = null, // e.g. "com.soapp"
    @ColumnInfo(name = "time_start") var auTimeStart: Long = 0, // timestamp of entering an app
    @ColumnInfo(name = "time_end") var auTimeEnd: Long = 0 // timestamp of exiting an app (includes screen lock)
) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "row") var auRow: Int? = null
}
