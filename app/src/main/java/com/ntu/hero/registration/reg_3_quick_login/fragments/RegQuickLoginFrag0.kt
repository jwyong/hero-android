package com.ntu.hero.registration.reg_3_quick_login.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.ntu.hero.R
import com.ntu.hero.databinding.Reg3QuickLoginFrag0Binding
import com.ntu.hero.global.BioMetricsHelper
import com.ntu.hero.global.GlobalVars
import com.ntu.hero.global.MPreferences
import com.ntu.hero.global.PermissionHelper

class RegQuickLoginFrag0 : Fragment() {
    private lateinit var binding: Reg3QuickLoginFrag0Binding

    // obs for touch and face id
    val obsShowTouchID = ObservableBoolean()
    val obsTouchIDAlreadyEnabled = ObservableBoolean()
    val obsShowFaceID = ObservableBoolean()

    // permissions
    private val permissionHelper = PermissionHelper()
    private val TOUCH_REQUEST_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.reg_3_quick_login_frag_0, container, false)
        binding.data = this

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        // check touchID status
        setupTouchID()
    }

    // disable/enable touch id btn
    private fun setupTouchID() {
        // for btn visibility/enabled state
        when (BioMetricsHelper().phoneSupportsTouchID(context)) {
            0 -> { // no touch hardware, don't show touch ID btn
                obsShowTouchID.set(false)
            }

            else -> { // show btn for other cases
                obsShowTouchID.set(true)
            }
        }

        // for btn text
        if (MPreferences.getIntValue(GlobalVars.PREF_TOUCH_ID_ENABLED) == 1) {
            obsTouchIDAlreadyEnabled.set(true)

        } else {
            obsTouchIDAlreadyEnabled.set(false)

        }
    }

    // functions for checking permissions (touchID, faceID btns)
    private fun checkPerms(permReqCode: Int): Boolean {
        lateinit var permStrArr: Array<String>
        var permStr = R.string.touch_perm

        when (permReqCode) {
            TOUCH_REQUEST_CODE -> { // touchID
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { // API 28
                    Log.d(GlobalVars.TAG1, "RegQuickLoginFrag0, checkPerms: api28")
                    permStrArr = arrayOf(
                        android.Manifest.permission.USE_BIOMETRIC
                    )
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // API 23
                    Log.d(GlobalVars.TAG1, "RegQuickLoginFrag0, checkPerms: api23")
                    permStrArr = arrayOf(
                        android.Manifest.permission.USE_FINGERPRINT
                    )
                }
                permStr = R.string.touch_perm
            }
        }

        if (!permissionHelper.hasPermissions(context!!, *permStrArr)) { // no perm
            Log.d(GlobalVars.TAG1, "RegQuickLoginFrag0, checkPerms: no perm")

            permissionHelper.askForPermissions(context!!, this, permStrArr, permReqCode, permStr, null)

            return false

        } else {
            Log.d(GlobalVars.TAG1, "RegQuickLoginFrag0, checkPerms: got perm")

            return true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // prep vars based on requestCode
        var deniedPopupMsg = R.string.touch_perm_denied
        var grantedFun = touchIDFunc
        when (requestCode) {
            TOUCH_REQUEST_CODE -> {
                deniedPopupMsg = R.string.touch_perm_denied
                grantedFun = touchIDFunc
            }
        }

        // show "open settings" popup if denied, do touchID actions if granted
        permissionHelper.onPermResults(
            context!!,
            grantResults,
            deniedPopupMsg,
            grantedFun
        )
    }

    // function for touchID
    private val touchIDFunc = Runnable {
        BioMetricsHelper().enableDisableTouchID(context, obsTouchIDAlreadyEnabled)
    }

    //===== databinding funcs
    fun pinCodeBtn(_v: View) {
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_regQuickLoginFrag0_to_regQuickLoginFrag1)

    }

    fun touchIDBtn(_v: View) {
        // check permissions first
        if (checkPerms(TOUCH_REQUEST_CODE)) { // got perms - run touch id actions
            touchIDFunc.run()
        }
    }

    fun faceIDBtn(_v: View) {
    }
}