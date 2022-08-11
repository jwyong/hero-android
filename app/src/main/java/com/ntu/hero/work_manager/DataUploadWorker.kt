package com.ntu.hero.work_manager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ntu.hero.api.api_clients.RetroAPIClient.api
import com.ntu.hero.api.api_models.DataUploadModel
import com.ntu.hero.api.helpers.RefreshTokenHelper
import com.ntu.hero.global.GlobalVars
import com.ntu.hero.global.MPreferences
import com.ntu.hero.sql.DatabaseHelper

// post to server once daily
class DataUploadWorker(private val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private val refreshTokenHelper = RefreshTokenHelper()
    private val callLogsHelper = CallLogsHelper()

    // do posting to server
    override fun doWork(): Result {
        val dataTypes = inputData.getStringArray("dataTypes")

        Log.d(
            GlobalVars.TAG1,
            "DataUploadWorker: doWork"
        )

        // refresh access token first
        if (refreshTokenHelper.retroRefreshToken(context)) {
            // loop post data from sqlite to server (other than call logs)
            val header = MPreferences.getAccessToken()!!
            var dataList: List<*>?
            var dataType: String
            var result = Result.success()

            dataTypes?.forEachIndexed { index, s ->
                dataType = dataTypes[index]
                dataList = DatabaseHelper.getDataList(dataType)

                // ONLY do post if got data (list not empty)
                if (!dataList.isNullOrEmpty()) {
                    result = postDataToServer(header, dataType, dataList!!)
                }
            }

            // get call logs then upload
            // TEMP - disabled for google play permissions policy rejection
//            dataList = callLogsHelper.getCallLogsList(context)
//            if (!dataList.isNullOrEmpty()) {
//                Log.d(GlobalVars.TAG1, "DataUploadWorker: doWork call logs not null")
//
//                postDataToServer(header, GlobalVars.DATA_TYPE_CALL_LOGS, dataList!!)
//            }

            Log.d(GlobalVars.TAG1, "DataUploadWorker: doWork done looping")

            return result

        } else {
            Log.d(GlobalVars.TAG1, "DataUploadWorker: doWork refresh token failed")

            return Result.failure()

        }
    }

    private fun postDataToServer(header: String, dataType: String, dataList: List<*>): Result {
        var result = Result.success()

        Log.d(GlobalVars.TAG1, "DataUploadWorker: doWork dataType = $dataType")

        val response = api.postData(
            header,
            DataUploadModel(
                dataType = dataType, dataList = dataList
            )
        ).execute()

        if (response.isSuccessful) {
            Log.d(
                GlobalVars.TAG1,
                "DataUploadWorker: doWork success = ${response.body()}"
            )

            if (dataType.equals(GlobalVars.DATA_TYPE_CALL_LOGS)) {
                // set pref for call log timestamp if case if call logs
                MPreferences.save(
                    GlobalVars.PREF_CALL_LOGS_LAST_TIME,
                    System.currentTimeMillis().toString()
                )

            } else {
                // clear relevant db if not call logs
                DatabaseHelper.clearDataTable(dataType)
            }


        } else {
            Log.d(
                GlobalVars.TAG1,
                "DataUploadWorker: doWork failed = ${response.errorBody()}"
            )

            result = Result.failure()
        }
        return result
    }
}