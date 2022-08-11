package com.ntu.hero.appt.appt_schedule

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.ntu.hero.R
import com.ntu.hero.databinding.ApptScheActiBinding
import com.ntu.hero.global.BaseActivity

class ApptScheActi : BaseActivity(), ApptScheActiInterface {
    lateinit var binding: ApptScheActiBinding

    private lateinit var frag: Fragment

    val obsRoomTitle = ObservableField<String>()
    private lateinit var srvName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.appt_sche_acti)
        binding.data = this

        srvName = intent.getStringExtra("srvName")

        frag = supportFragmentManager.findFragmentById(R.id.main_frag)!!

    }

    //===== interface funcs
    override fun getSrvName(): String {
        return srvName
    }

    override fun setRoomTitle(title: String) {
        obsRoomTitle.set(title)
    }

    override fun navigateTo(actionID: Int, bundle: Bundle) {
        Navigation.findNavController(frag.view!!).navigate(actionID, bundle)
    }

    override fun finishActi() {
        finish()
    }

    override fun onBackPressed() {
        if (Navigation.findNavController(frag.view!!).navigateUp()) { // close fragment

        } else { // close activity
            finish()

        }
    }
}

// interface for communication with fragments
interface ApptScheActiInterface {
    fun getSrvName(): String

    fun setRoomTitle(title: String)

    fun navigateTo(actionID: Int, bundle: Bundle)

    fun finishActi()
}
