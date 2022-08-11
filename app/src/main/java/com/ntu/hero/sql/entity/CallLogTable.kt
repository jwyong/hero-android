package com.ntu.hero.sql.entity

data class CallLogTable(
    var contactID: String? = null, // uuid of phone number based on ContactID table
    var callType: String? = null, // e.g. in, out, miss
    var callTimestamp: String? = null, // timestamp of call
    var callDuration: String? = null // call duration
)