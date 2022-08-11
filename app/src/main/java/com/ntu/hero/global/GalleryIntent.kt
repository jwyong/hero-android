package com.ntu.hero.global

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

class GalleryIntent {
    // open camera - cancel if no permissions
    fun launchGallery(context: Context, requestCode: Int) {
        lateinit var intent: Intent

        // set gallery file type based on type
        when (requestCode) {
            2 -> { // img
                intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
            }
        }

        startActivityForResult(context as Activity, intent, requestCode, null)
    }

    // gallery activity results (img)
    fun galleryImgActiResults(context: Context, data: Intent?): File? {
        val fileUri = data?.data
        var oriFile: File? = null

        // only continue if file uri is not null
        if (fileUri != null) {
            val filePath = getFilePathFromUri(context, fileUri)

            // get file based on uri
            if (filePath != null) {
                oriFile = File(filePath)

            } else {
                oriFile = File(fileUri.path)
            }
        }

        return oriFile
    }

    // gallery (docs) activity results
    fun galleryDocsActiResults(context: Context, data: Intent?): File? {
        val oriFileUri = data?.data

        // check scheme (content or file)
        var outFile: File? = null
        when (data?.scheme) {
            "content" -> { // content - get file via input stream
                // get file and fileName
                val oriFileName = DocumentFile.fromSingleUri(context, oriFileUri!!)?.name

                // prepare input stream
                val contentResolver = context.contentResolver

                try {
                    val inputStream = contentResolver.openInputStream(oriFileUri)

                    // prepare output stream based on sent file path
                    outFile = DirectoryHelper().saveFile(context, 24, oriFileName!!)

                    val outputStream = FileOutputStream(outFile)

                    // copy input stream to output stream
                    inputStream.copyTo(outputStream, 1024)
                } catch (e: FileNotFoundException) {
//                    MiscHelper().toastMsg(context, R.string.file_not_exist, Toast.LENGTH_SHORT)
                }
            }

            "file" -> {

                val oriFile = File(oriFileUri?.path)
                val oriFileName = oriFile.name

                outFile = DirectoryHelper().saveFile(context,24, oriFileName)

                // only need copy file if doesn't already exist
//                if (!outFile!!.exists()) {
                oriFile.copyTo(outFile!!)
//                }
            }
        }

        return outFile
    }

    // get file path from gallery picker
    private fun getFilePathFromUri(context: Context, uri: Uri): String? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null)

        var filePath: String? = null
        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            filePath = cursor.getString(columnIndex)
        }
        cursor?.close()

        return filePath
    }

}

