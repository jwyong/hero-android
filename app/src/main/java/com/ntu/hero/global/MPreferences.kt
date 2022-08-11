package com.ntu.hero.global

import android.content.Context
import com.ntu.hero.Hero

object MPreferences {
    private val PREFS_NAME = "Hero"

    private var mPreferences = Hero.instance?.applicationContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    //=== save value to pref
    fun save(strPrefName: String, strData: String) {
        val editor = mPreferences?.edit()
        editor?.putString(strPrefName, strData)
        editor?.apply()
    }

    fun saveint(strPrefName: String, intData: Int) {
        val editor = mPreferences?.edit()
        editor?.putInt(strPrefName, intData)
        editor?.apply()
    }

    fun saveLong(strPrefName: String, longData: Long) {
        val editor = mPreferences?.edit()
        editor?.putLong(strPrefName, longData)
        editor?.apply()
    }

    //=== get value from pref
    fun getValue(strPrefName: String): String? {
        return mPreferences?.getString(strPrefName, null)
    }

    fun getIntValue(strPrefName: String): Int? {
        return mPreferences?.getInt(strPrefName, -1)
    }

    fun getLongValue(strPrefName: String): Long? {
        return mPreferences?.getLong(strPrefName, -1)
    }

    //=== functions for direct access
    fun getRegStageInt(): Int? {
        return mPreferences?.getInt(GlobalVars.PREF_REG_STG, 0)
    }

    fun setRegStageInt(regStageInt: Int) {
        val editor = mPreferences?.edit()
        editor?.putInt(GlobalVars.PREF_REG_STG, regStageInt)
        editor?.apply()
    }

    fun getAccessToken(): String? {
        return "Bearer " + mPreferences?.getString(GlobalVars.PREF_ACCESS_TOKEN, null)
    }

    fun setAccessToken(accessToken: String) {
        val editor = mPreferences?.edit()
        editor?.putString(GlobalVars.PREF_ACCESS_TOKEN, accessToken)
        editor?.apply()
    }

    fun getProfileName(): String? {
        return mPreferences?.getString(GlobalVars.PREF_USER_NAME, null)
    }

    fun setProfileName(accessToken: String) {
        val editor = mPreferences?.edit()
        editor?.putString(GlobalVars.PREF_USER_NAME, accessToken)
        editor?.apply()
    }

    fun getProfileImg(): String? {
        return mPreferences?.getString(GlobalVars.PREF_USER_PROFILE_IMG, null)
    }

    fun setProfileImg(accessToken: String) {
        val editor = mPreferences?.edit()
        editor?.putString(GlobalVars.PREF_USER_PROFILE_IMG, accessToken)
        editor?.apply()
    }
}