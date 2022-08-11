package com.ntu.hero.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ntu.hero.R
import com.ntu.hero.appt.ApptMainActi
import com.ntu.hero.chat.ChatActi
import com.ntu.hero.databinding.HomeActiBinding
import com.ntu.hero.global.MPreferences
import com.ntu.hero.global.PermissionHelper
import com.ntu.hero.profile.ProfileMainActi
import com.ntu.hero.sql.DatabaseHelper
import com.ntu.hero.sql.entity.HomeActivityList
import com.ntu.hero.work_manager.InitDataWM
import kotlinx.android.parcel.Parcelize

class HomeActi : AppCompatActivity() {
    private lateinit var binding: HomeActiBinding
    private val permissionHelper = PermissionHelper()
    private val PERMS_REQ_CODE = 1

    //===== recyclerview
    private val rvAdapter = HomeAdapter()

    //===== obs
    // obs: profile
    val obsProfileName = ObservableField<String>(MPreferences.getProfileName())
    val obsProfileImg = ObservableField<String>(MPreferences.getProfileImg())
    val obsApptNoti = ObservableInt(3)

    // obs: title
    val obsTitle = ObservableField<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // check for all permissions
        checkForAllPerms()

        // save stage int
        MPreferences.setRegStageInt(5)

        // init work manager for all (data collection + uploading)
        InitDataWM(this).initDataWM()

        // TEMP - add 3 items to sqlite if dont hv yet
        val alreadyAdded = MPreferences.getIntValue("homeActiAdded")
        if (alreadyAdded != 1) {
            DatabaseHelper.populateHomeActiList(this)
        }

        binding = DataBindingUtil.setContentView(this, R.layout.home_acti)
        binding.data = this

        // set title
        obsTitle.set(getString(R.string.home_title))

        // set activities list
        setupRV()
        setupLiveData()
    }

    //===== databinding funcs
    fun profileBtnOnClick(_v: View) {
        val intent = Intent(this, ProfileMainActi::class.java)
        startActivity(intent)
    }

    fun notiBadgeOnClick(_v: View) {
        // TEMP - populate acti list
        DatabaseHelper.populateHomeActiList(this)
    }

    fun chatBtnOnClick(_v: View) {
        val intent = Intent(this, ChatActi::class.java)
        startActivity(intent)
    }

    fun apptBtnOnClick(_v: View) {
        val intent = Intent(this, ApptMainActi::class.java)
        startActivity(intent)
    }

    //===== normal funcs
    private fun checkForAllPerms() {
        val permStrArr = arrayOf(
            // GPS
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION

            // Call Logs
//            android.Manifest.permission.READ_CALL_LOG
        )

        // check for perms
        if (!permissionHelper.hasPermissions(this, *permStrArr)) { // no perm
            // straight request permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permStrArr, PERMS_REQ_CODE)
            }
        }

        // check for access perms
        permissionHelper.isAccessibilitySettingsOn(this)
    }

    private fun setupRV() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActi)

            rvAdapter.setHasStableIds(true)
            adapter = rvAdapter
        }
    }

    private fun setupLiveData() {
        val vm = ViewModelProviders.of(this).get(HomeVM::class.java)
        vm.list.observe(this, object : Observer<List<HomeActivityList>?> {
            override fun onChanged(t: List<HomeActivityList>?) {
                rvAdapter.submitList(t)
            }
        })
    }

}

@Parcelize
class AnswerModelParce : Parcelable {
    var answerIndex: String? = null
    var answer: String? = null
}
