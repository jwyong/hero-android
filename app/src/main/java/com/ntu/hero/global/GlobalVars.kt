package com.ntu.hero.global

import android.os.Environment
import com.ntu.hero.Hero

object GlobalVars {
    // basic project strings
    const val APP_NAME = "Hero"
    const val DB_NAME = "HeroDB"
    const val TAG1 = "JAY"

    const val AND_KEY_STORE = "AndroidKeyStore"
    const val ENCRYPT_TRANS = "AES/GCM/NoPadding"
    const val KEY_STORE_ALIAS = "heroSoappKeyAlias"

    const val G_MAPS_API_KEY = "AIzaSyAqxDYFLzNwHGu9cElZKu6FHgkplZ08wgQ"

    //===== preferences strings
    // ENCRYPTION
    const val PREF_IV = "PREF_IV"
    const val SHIO = "ajefa6tc73t6raiw7tr63wi3r7citrawcirtcdg78o2vawri7t"
    const val PF = "504pp157h3fu7u63"

    const val PREF_PASSCODE_UPLOADED = "PREF_PASSCODE_UPLOADED"
    const val PREF_LAST_ACTIVE = "PREF_LAST_ACTIVE"
    const val PREF_REG_STG = "PREF_REG_STG"
    const val PREF_SMS_TOKEN = "PREF_SMS_TOKEN"
    const val PREF_REG_TOKEN = "PREF_REG_TOKEN"
    const val PREF_CHAT_BG_COLOUR = "PREF_CHAT_BG_COLOUR"
    const val PREF_CHAT_TEXT_COLOUR = "PREF_CHAT_TEXT_COLOUR"

    //pref: user info
    const val PREF_EMAIL = "PREF_EMAIL"
    const val PREF_COUNTRY_CODE = "PREF_COUNTRY_CODE"
    const val PREF_PHONE = "PREF_PHONE"
    const val PREF_USER_NAME = "PREF_USER_NAME"
    const val PREF_USER_PROFILE_IMG = "PREF_USER_PROFILE_IMG"
    const val PREF_USER_ROLE = "PREF_USER_ROLE"

    const val PREF_TOKEN_TYPE = "PREF_TOKEN_TYPE"
    const val PREF_ACCESS_TOKEN = "PREF_ACCESS_TOKEN"
    const val PREF_ACCESS_TOKEN_EXPIRES_IN = "PREF_ACCESS_TOKEN_EXPIRES_IN"
    const val PREF_REFRESH_TOKEN = "PREF_REFRESH_TOKEN"

    // pref: biometrics, passcodes
    const val PREF_PINCODE = "PREF_PINCODE"
    const val PREF_TOUCH_ID_ENABLED = "PREF_TOUCH_ID_ENABLED"

    // pref: chatbot
    const val PREF_SESSION_ID = "PREF_SESSION_ID"

    // pref: data collection
    const val PREF_CALL_LOGS_LAST_TIME = "PREF_CALL_LOGS_LAST_TIME"

    //===== directory
    const val DEFAULT_FILE_DATE_FORMAT = "yyyyMMdd_HHmmss"
    const val TEMP_IMG_NAME = "temp.jpg"

    // folder names
    const val PROFILE_IMG_DIR = "Profile"
    const val PROFILE_IMG_PREFIX = "PR_"

    // folder paths
    val PROFILE_IMG_PATH =
        Environment.getExternalStorageDirectory().toString() + "/$APP_NAME/$PROFILE_IMG_DIR/"
    val TEMP_PATH = Hero.instance?.applicationContext?.cacheDir.toString() + "/$APP_NAME/"


    //===== chat room (dynamic)
    val chat_setup_theme_1 = "\uD83D\uDC4B Hello, ${MPreferences.getProfileName()}! Before we start, would you like to customise this chat?"
    val chat_start = "\uD83D\uDC4B Welcome back, ${MPreferences.getProfileName()}! Hang on while I check for any available questionnaires for you."
    val chat_submit_title = "Great Job ${MPreferences.getProfileName()}, \nyou finished the Questionnaire"


    //===== DataCollection
    //=== motion capture (activity transition)
    const val MC_NAME = "hero_motion_capture"

    //=== worker constants
    const val WM_UPLOAD = "WM_UPLOAD"
    const val WM_GPS_MOTION = "WM_GPS_MOTION"

    //=== DU: posting constants
    const val DATA_TYPE_SCREEN_USE = "screen_use"
    const val DATA_TYPE_APP_USE = "app_use"
    const val DATA_TYPE_KEYBOARD_USE = "keyboard_use"
    const val DATA_TYPE_LOCATION = "location"
    const val DATA_TYPE_MOTION = "motion"
    const val DATA_TYPE_CALL_LOGS = "call_logs"

}