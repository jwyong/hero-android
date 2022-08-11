package com.ntu.hero.registration.reg_4_profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.ntu.hero.R
import com.ntu.hero.api.api_clients.RetroAPIClient
import com.ntu.hero.api.api_models.BasicResponseModel
import com.ntu.hero.databinding.Reg4ProfileActiBinding
import com.ntu.hero.global.*
import com.ntu.hero.home.HomeActi
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RegProfileActi : AppCompatActivity() {
    // helpers
    private val uiHelper = UIHelper()
    private val permissionHelper = PermissionHelper()
    private val cameraIntent = CameraIntent()
    private val galleryIntent = GalleryIntent()
    private val imgProcessHelper = ImgProcessHelper()
    private val miscHelper = MiscHelper()
    private val api = RetroAPIClient.api

    private lateinit var binding: Reg4ProfileActiBinding

    // for btn
    val isBtnEnabled = ObservableBoolean(false)
    val isEditTxtEnabled = ObservableBoolean(true)
    val obsEditTxt = ObservableField<String>()

    // for profile img
    val obsProfileImg = ObservableField<String>()
    private var profileImgFile: File? = null
    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // save stage int
        MPreferences.setRegStageInt(4)

        // setup data if already got
        setupExistingData()

        binding = DataBindingUtil.setContentView(this, R.layout.reg_4_profile_acti)
        binding.data = this
    }

    private fun setupExistingData() {
        // set observable for inputtxt
        obsEditTxt.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val name = obsEditTxt.get() ?: ""
                if (name.isNotEmpty()) { // got value, can send
                    isBtnEnabled.set(true)

                    // save inputted name first
                    MPreferences.save(GlobalVars.PREF_USER_NAME, name)
                } else {
                    isBtnEnabled.set(false)

                }
            }
        })

        // set name if already type before
        obsEditTxt.set(MPreferences.getValue(GlobalVars.PREF_USER_NAME))

        // set img if already taken before
        obsProfileImg.set(MPreferences.getValue(GlobalVars.PREF_USER_PROFILE_IMG))
    }

    // check perms func
    private fun checkPerms(permReqCode: Int): Boolean {
        lateinit var permStrArr: Array<String>
        var permStr = R.string.camera_perm

        when (permReqCode) {
            CAMERA_REQUEST_CODE -> {
                permStrArr = arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                permStr = R.string.camera_perm
            }

            GALLERY_REQUEST_CODE -> {
                permStrArr = arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                permStr = R.string.gallery_perm
            }
        }

        if (!permissionHelper.hasPermissions(this, *permStrArr)) { // no perm
            permissionHelper.askForPermissions(this, null, permStrArr, permReqCode, permStr, null)

            return false

        } else {

            return true
        }
    }

    private fun compressAndSaveImg(fileToCompress: File) {
        profileImgFile =
            imgProcessHelper.compressImg(this, fileToCompress, 1, 720, 50) // 1 = profile

        Log.d(
            GlobalVars.TAG1,
            "RegProfileActi, onActivityResult: compfileName = ${profileImgFile?.absolutePath} compFileSize = ${profileImgFile?.length()}"
        )

        if (profileImgFile != null) {
            val compFilePath = profileImgFile!!.absolutePath

            // get base64 from file
            val compByteStr =
                imgProcessHelper.createBase64FromImg(compFilePath, 75)

            MPreferences.save(GlobalVars.PREF_USER_PROFILE_IMG, compByteStr ?: "")
            obsProfileImg.set(compByteStr)
        }
    }

    //===== databinding funcs
    // submit access code to server
    fun createBtn(_v: View) {
        // update UI
        isBtnEnabled.set(false)
        isEditTxtEnabled.set(false)

        // post access code to server
        retroPostCreateProfile(obsEditTxt.get()!!)
    }

    fun profileImgOnClick(_v: View) {
        uiHelper.btmDialog2Items(
            this,
            R.string.take_photo,
            R.string.photo_gallery,
            takePhotoFunc,
            openGalleryFunc,
            true
        )
    }

    //===== runnable funcs
    private val takePhotoFunc = Runnable {
        // check camera permissions
        if (checkPerms(CAMERA_REQUEST_CODE)) {
            // launch camera if already got permissions
            cameraIntent.launchCamera(this, CAMERA_REQUEST_CODE, 0, true)
        }
    }

    private val openGalleryFunc = Runnable {
        // check camera permissions
        if (checkPerms(GALLERY_REQUEST_CODE)) {
            // launch gallery if already got permissions
            galleryIntent.launchGallery(this, GALLERY_REQUEST_CODE)
        }
    }

    //===== results
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // prep vars based on requestCode
        var deniedPopupMsg = 0
        var grantedFun = Runnable {}
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                deniedPopupMsg = R.string.camera_perm_denied
                grantedFun = takePhotoFunc
            }

            GALLERY_REQUEST_CODE -> {
                deniedPopupMsg = R.string.gallery_perm_denied
                grantedFun = openGalleryFunc
            }
        }

        // show "open settings" popup if denied
        permissionHelper.onPermResults(
            this,
            grantResults,
            deniedPopupMsg,
            grantedFun
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val file: File?
                when (requestCode) {
                    CAMERA_REQUEST_CODE -> {
                        file = File(GlobalVars.TEMP_PATH, GlobalVars.TEMP_IMG_NAME)
                    }

                    GALLERY_REQUEST_CODE -> {
                        file = galleryIntent.galleryImgActiResults(this, data)
                    }

                    else -> {
                        file = File(GlobalVars.TEMP_PATH, GlobalVars.TEMP_IMG_NAME)
                    }
                }

                if (file != null) {
                    compressAndSaveImg(file)

                } else { // intent problem
                    miscHelper.toastMsgInt(this, R.string.something_went_wrong, Toast.LENGTH_SHORT)
                }

            }
        }
    }

    //===== Retro funcs
    private fun retroPostCreateProfile(userName: String) {
        // header
        val header = MPreferences.getAccessToken()!!

        // body
        val profileName = MultipartBody.Part.createFormData("profile_name", userName)
        val profileImgReqBody: RequestBody
        val gotImg: Int // 0 = no changes to img, 1 = got img, 2 = remove img
        if (profileImgFile == null) {
            profileImgReqBody = RequestBody.create(MediaType.parse("image/jpeg"), "")
            gotImg = 0
        } else {
            profileImgReqBody = RequestBody.create(MediaType.parse("image/jpeg"), profileImgFile!!)
            gotImg = 1
        }
        val uploadCode = MultipartBody.Part.createFormData("upload_code", gotImg.toString())
        val profileImgMultiPart =
            MultipartBody.Part.createFormData("profile_picture", "profile.jpg", profileImgReqBody)

        api.postProfile(header = header, profileName = profileName, uploadCode = uploadCode, profileImg = profileImgMultiPart).enqueue(object :
            Callback<BasicResponseModel> {
            override fun onResponse(
                call: Call<BasicResponseModel>,
                response: Response<BasicResponseModel>
            ) {
                if (!response.isSuccessful) {
                    retroRespUnsuc(response)
                    return
                }

                retroRespSuccess(null)
            }

            override fun onFailure(call: Call<BasicResponseModel>, t: Throwable) {
                retroFailure(t)
            }
        })
    }

    //===== retro responses
    private fun retroRespSuccess(response: Response<BasicResponseModel>?) {
        // go to next acti
        val intent = Intent(this, HomeActi::class.java)
        startActivity(intent)

        finishAffinity()
    }

    private fun retroRespUnsuc(response: Response<*>) {
        // update UI
        isBtnEnabled.set(true)
        isEditTxtEnabled.set(true)

        miscHelper.handleUnsuccError(this, "retroPostCreateProfile", response)
    }

    private fun retroFailure(throwable: Throwable) {
        // update UI
        isBtnEnabled.set(true)
        isEditTxtEnabled.set(true)

        miscHelper.handleFailureError(this, "retroPostCreateProfile", throwable)
    }
}