package com.ntu.hero.sql.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class KeyboardUsage(
    @ColumnInfo(name = "time_start") var keyTimeStart: Long = 0, // start time of session
    @ColumnInfo(name = "time_end") var keyTimeEnd: Long = 0, // end time of session
    @ColumnInfo(name = "total_keystrokes") var keyTotalStrokes: Int = 0, // total number of keystrokes in this session
    @ColumnInfo(name = "total_backspaces") var keyTotalBackspaces: Int = 0, // total number of backspace clicked
    @ColumnInfo(name = "total_autocorrect") var keyTotalAutocorrect: Int = 0 // total number of autocorrect events
) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "row") var keyRow: Int? = null
}
