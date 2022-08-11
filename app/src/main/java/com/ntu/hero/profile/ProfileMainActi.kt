package com.ntu.hero.profile

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.ntu.hero.R
import com.ntu.hero.databinding.ProfileMainActiBinding
import com.ntu.hero.global.BaseActivity

class ProfileMainActi : BaseActivity(), ProfileMainInterface {
    lateinit var binding: ProfileMainActiBinding

    private lateinit var frag: Fragment

    val obsRoomTitle = ObservableField<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setup main layout
        binding = DataBindingUtil.setContentView(this, R.layout.profile_main_acti)
        binding.data = this

        // setup toolbar
        setupBackText()

        setRoomTitle(getString(R.string.profile))

        frag = supportFragmentManager.findFragmentById(R.id.main_frag)!!
    }

    //===== interface funcs
    override fun setRoomTitle(title: String) {
        obsRoomTitle.set(title)
    }

    override fun navigateTo(actionID: Int, bundle: Bundle) {
        Navigation.findNavController(frag.view!!).navigate(actionID, bundle)
    }

    override fun finishAll() {
        finishAffinity()
    }
}

// interface for communication with fragments
interface ProfileMainInterface {
    fun setRoomTitle(title: String)

    fun navigateTo(actionID: Int, bundle: Bundle)

    fun finishAll()
}