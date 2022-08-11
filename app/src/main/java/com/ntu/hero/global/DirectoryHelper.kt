package com.ntu.hero.global

import android.content.Context
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class DirectoryHelper {
    // check if directory exists
    private fun checkAndCreateFolder(type: Int) {
        lateinit var newFolder: File

        // need to use non-concatenated string for path when creating dirs
        when (type) {
            0 -> { // camera temp
                newFolder = File(GlobalVars.TEMP_PATH, ".nomedia")
            }

            1 -> { // profile img
                newFolder = File(GlobalVars.PROFILE_IMG_PATH, ".nomedia")
            }
        }

        // create dir if exist
        if (!newFolder.exists()) {
            val dirs = newFolder.mkdirs()
        }
    }

    // save file to path (check if directory created first
    @Synchronized
    fun saveFile(context: Context, type: Int, fileFormatOrFullName: String): File? {
        // no permissions means return false/null
        if (!checkPerm(context)) {
            // return null if no permission
            return null
        }

        // check if dir exists first
        checkAndCreateFolder(type)

        // save file to path based on type
        val timeStamp: String =
            SimpleDateFormat(GlobalVars.DEFAULT_FILE_DATE_FORMAT, Locale.getDefault()).format(Date())

        lateinit var file: File
        when (type) {
            0 -> { // camera temp
                return File(GlobalVars.TEMP_PATH, GlobalVars.TEMP_IMG_NAME)
            }

            1 -> { // profile img
                file = File(
                    GlobalVars.PROFILE_IMG_PATH,
                    "${GlobalVars.PROFILE_IMG_PREFIX}$timeStamp.$fileFormatOrFullName"
                )
            }

            else -> {
                return null
            }
        }

        // check if file already exists
        return checkFileExist(context, type, fileFormatOrFullName, file)
    }

    // create new file if already exists
    private fun checkFileExist(
        context: Context,
        type: Int,
        fileFormatOrFullName: String,
        file: File
    ): File? {
        // no need check for docs since same name
        return if (file.exists()) {
            saveFile(context, type, fileFormatOrFullName)

        } else {
            file
        }
    }

    // check storage permissions
    private fun checkPerm(context: Context): Boolean {
        val permStrArr = arrayOf(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (PermissionHelper().hasPermissions(context, *permStrArr)) {
            return true

        } else {
            // toast permission msg
            Log.d(GlobalVars.TAG1, "DirectoryHelper, checkPerm: No Perm")
            return false
        }
    }
}