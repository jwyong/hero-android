package com.ntu.hero.profile.frags

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import com.ntu.hero.R
import com.ntu.hero.databinding.ProfileFrag0MainBinding
import com.ntu.hero.global.GlobalVars
import com.ntu.hero.global.MPreferences
import com.ntu.hero.global.MiscHelper
import com.ntu.hero.global.PermissionHelper
import com.ntu.hero.profile.ProfileMainActi
import com.ntu.hero.profile.ProfileMainInterface

class ProfileFrag0Main : Fragment() {
    private lateinit var binding: ProfileFrag0MainBinding
    private var mView: View? = null

    // for acti-frag comm
    lateinit var fragActiInterface: ProfileMainInterface

    private val permissionHelper = PermissionHelper()
    private val miscHelper = MiscHelper()

    val obsProfileName = ObservableField<String>(MPreferences.getProfileName())
    val obsProfileImg = ObservableField<String>(MPreferences.getProfileImg())
    val obsRole =
        ObservableField<String>(MPreferences.getValue(GlobalVars.PREF_USER_ROLE) ?: "ROLE")

    override fun onAttach(context: Context) {
        super.onAttach(context)

        fragActiInterface = (context as ProfileMainActi)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mView == null) {
            binding =
                DataBindingUtil.inflate(
                    inflater,
                    R.layout.profile_frag0_main,
                    container,
                    false
                )
            binding.data = this
            binding.lifecycleOwner = this

            mView = binding.root
        }

        return mView
    }

    //===== databinding funcs
    fun profileItemOnclick(_v: View, title: String) {
        // set room title
        fragActiInterface.setRoomTitle(title)

        // navigate to
        when (title) {
            getString(R.string.profile_settings) -> {
//                fragActiInterface.navigateTo()
            }

            // review permissions - go to android settings
            getString(R.string.profile_perms) -> {
                if (permissionHelper.isAccessibilitySettingsOn(context!!)) {
                    miscHelper.toastMsgInt(context, R.string.access_srv_enabled, Toast.LENGTH_SHORT)
                }
            }
        }
    }

    fun logoutBtnOnClick(_v: View) {
        miscHelper.logout(context, Runnable {
            fragActiInterface.finishAll()
        })
    }

}