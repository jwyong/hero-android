package com.ntu.hero.sql.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class HomeActivityList @JvmOverloads constructor(
    var homeTitle: String? = null,
    var homeDesc: String? = null,
    var homeImg: String? = null
) {

    @PrimaryKey(autoGenerate = true)
    var homeRow: Int? = null
}
