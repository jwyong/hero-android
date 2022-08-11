package com.ntu.hero.registration.reg_0_on_board.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ntu.hero.R
import com.ntu.hero.databinding.Reg0OnBoardFrag1Binding
import com.ntu.hero.global.GlobalVars
import com.ntu.hero.global.PermissionHelper
import com.ntu.hero.global.UIHelper
import com.ntu.hero.registration.reg_0_on_board.RegOnBoardActi
import com.ntu.hero.registration.reg_1_access_code.RegAccessCodeActi

class RegOnBoardFrag1 : Fragment() {
    private lateinit var binding: Reg0OnBoardFrag1Binding

    // helpers
    private val permissionHelper = PermissionHelper()
    private val uiHelper = UIHelper()

    // permissions
    private val PERMS_REQ_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.reg_0_on_board_frag_1, container, false)
        binding.data = this

        return binding.root
    }

    //===== databinding funcs
    fun acceptBtn(_v: View?) {
        // do permissions checking
        if (checkPerms()) { // got perms - check if got accessibility
            if (checkAccess()) { // got access, straight run accept btn func
                acceptBtnRunnable.run()
            }
        }
    }

    fun rejectBtn(_v: View) {
        uiHelper.dialog1btn(context!!, R.string.do_not_perm_msg, R.string.grant_perms, acceptBtnRunnable, true)
    }

    //===== normal funcs
    private val acceptBtnRunnable = Runnable {
        if (checkPerms()) { // got perms - check if got accessibility
            if (checkAccess()) { // got access, straight run accept btn func
                val intent = Intent(context, RegAccessCodeActi::class.java)
                startActivity(intent)

                (context as RegOnBoardActi).finish()
            }
        }
    }

    // check for normal permissions (gps, storage, etc)
    private fun checkPerms(): Boolean {
        val permStrArr = arrayOf(
            // GPS
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION

            // Call Logs
            // TEMP - disabled due to google policy
//            android.Manifest.permission.READ_CALL_LOG
        )

        if (!permissionHelper.hasPermissions(context!!, *permStrArr)) { // no perm
            // straight request permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permStrArr, PERMS_REQ_CODE)
            }

            return false

        } else {
            return true
        }
    }

    // check if accessibility is enabled
    private fun checkAccess(): Boolean {
        return permissionHelper.isAccessibilitySettingsOn(context!!)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Log.d(GlobalVars.TAG1, "RegOnBoardFrag1, onRequestPermissionsResult: ")

        // show "open settings" popup if denied, do acceptBtn actions if granted
        permissionHelper.onPermResults(
            context!!,
            grantResults,
            R.string.do_not_perm_msg,
            acceptBtnRunnable
        )
    }
}