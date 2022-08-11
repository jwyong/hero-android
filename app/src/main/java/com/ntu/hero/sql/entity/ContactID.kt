package com.ntu.hero.sql.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ContactID(
    var cUniqueID: String? = null,
    var cPhoneNumber: String? = null
) {

    @PrimaryKey(autoGenerate = true)
    var cRow: Int? = null
}
