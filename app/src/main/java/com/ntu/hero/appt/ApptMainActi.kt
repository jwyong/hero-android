package com.ntu.hero.appt

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ntu.hero.R
import com.ntu.hero.appt.appt_schedule.ApptScheActi
import com.ntu.hero.chat.fragments.circles_hori.ApptMainActiAdapter
import com.ntu.hero.databinding.ApptMainActiBinding
import com.ntu.hero.global.BaseActivity
import com.ntu.hero.global.GlobalVars
import com.ntu.hero.sql.entity.Appointment
import jp.wasabeef.blurry.Blurry

class ApptMainActi : BaseActivity() {
    lateinit var binding: ApptMainActiBinding

    // selected appointment item (when click on appt item)
    val obsSelApptItem = ObservableField<Appointment>()
    val obsBtnLabel = ObservableField<String>()
    val obsIsCancelling = ObservableBoolean(false)

    private val mAdapter = ApptMainActiAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setup main layout
        binding = DataBindingUtil.setContentView(this, R.layout.appt_main_acti)
        binding.data = this

        // setup toolbar
        setupBackText()

        // setup rv
        setupRV()
        setupLiveData()

        setupObs()
    }

    //===== funcs
    private fun setupRV() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)

            mAdapter.setHasStableIds(true)
            adapter = mAdapter
        }
    }

    private fun setupLiveData() {
        val vm = ViewModelProviders.of(this).get(ApptMainActiVM::class.java)
        vm.apptList.observe(this, Observer { t ->
            mAdapter.submitList(t)
        })
    }

    private fun setupObs() {
        // set initial btn label first
        obsBtnLabel.set(getString(R.string.appt_resche))

        // change btn text when cancelling
        obsIsCancelling.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (obsIsCancelling.get()) { // is cancelling, change btn text to "yes, cancel it"
                    obsBtnLabel.set(getString(R.string.cancel_appt_confirm))
                } else {
                    obsBtnLabel.set(getString(R.string.appt_resche))
                }
            }

        })
    }

    //===== normal funcs
    fun showApptDetpopup(item: Appointment) {
        // blur bg
        Blurry.with(this).capture(binding.root).into(binding.vBlurredBg)

        // show ui
        obsSelApptItem.set(item)
    }

    //===== databinding funcs
    fun createNewAppt(_v: View, serviceName: String) {
        val intent = Intent(this, ApptScheActi::class.java)
        intent.putExtra("srvName", serviceName)
        startActivity(intent)
    }

    //=== incl funcs
    fun rescheBtnOnClick() {
        // go to sche acti
        Log.d(GlobalVars.TAG1, "ApptMainActi: rescheBtnOnClick ")
    }

    fun cancelAppt(int: Int) {
        // hide resche btn and show confirmation
        obsIsCancelling.set(true)

        Log.d(GlobalVars.TAG1, "ApptMainActi: cancelAppt ")
    }

    // when click on close btn in appt det page
    fun hideApptDetPopup(_v: View?) {
        // reset all obs
        obsSelApptItem.set(null)
        obsIsCancelling.set(false)
        obsBtnLabel.set(getString(R.string.appt_resche))
    }

    //===== override funcs
    override fun onBackPressed() {
        // close popup if got
        if (obsSelApptItem.get() != null) {
            hideApptDetPopup(null)
        } else {
            super.onBackPressed()
        }
    }
}