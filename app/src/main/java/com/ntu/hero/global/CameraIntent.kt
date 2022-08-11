package com.ntu.hero.global

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.FileProvider
import com.ntu.hero.R
import java.io.File
import java.io.IOException

class CameraIntent {
    private val imgProcessHelper = ImgProcessHelper()

    // open camera - cancel if no permissions
    fun launchCamera(context: Context, requestCode: Int, type: Int, isFrontCamera: Boolean) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            var photoFile: File?

            intent.resolveActivity(context.packageManager)?.also {
                photoFile = try {
                    // ori cam file should always be saved as temp img
                    // temp img can later be used to create permanent ori file and compressed sent file
                    DirectoryHelper().saveFile(context, type, "jpg")

                } catch (e: IOException) {
                    // Error occurred while creating the File
                    Log.e(GlobalVars.TAG1, "CameraIntent, launchCamera: IOException", e)
                    null
                }

                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context,
                        context.getString(R.string.app_fp_name),
                        it
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

                    if (isFrontCamera) {
                        intent.putExtra(
                            "android.intent.extras.CAMERA_FACING",
                            android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT
                        )
                        intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
                        intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
                    } else {
                        intent.putExtra(
                            "android.intent.extras.CAMERA_FACING",
                            android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK
                        )
                        intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 0)
                        intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", false)
                    }

                    startActivityForResult(context as Activity, intent, requestCode, null)
                }
            }
        }
    }

    // camera activity results (temp not used)
//    fun cameraActiResults(context: Context, type: Int): File {
//        val oriFile = File(GlobalVars.TEMP_PATH, GlobalVars.TEMP_IMG_NAME)
//
//        // compress snapped photo and store to folder based on type
//        when (type) {
//            1 -> { // profile photo
//
//            }
//        }
//        imgProcessHelper.compressImg(context, oriFile, type)
//
//        // temp - don't store ori file
////        val copyToPath: String
////        val filePrefix: String
////        when (type) {
////            1 -> { // profile
////                copyToPath = GlobalVars.PROFILE_IMG_PATH
////                filePrefix = GlobalVars.PROFILE_IMG_PREFIX
////            }
////
////            else -> {
////                copyToPath = GlobalVars.TEMP_PATH
////            }
////        }
////
////        // copy ori temp img to imges_path for permanent keeping - temp file is ALWAYS at imges_sent_path/temp.jpg
////        val timeStamp: String = SimpleDateFormat(GlobalVars.DEFAULT_FILE_DATE_FORMAT,
////                Locale.getDefault()).format(Date())
////        oriFile = oriFile.copyTo(File(copyToPath, "IMG_$timeStamp.jpg"))
////
////        // send broadcast to gallery
////        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(oriFile)))
//
//        return oriFile
//    }
}

