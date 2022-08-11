package com.ntu.hero.appt.appt_schedule.frags

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import com.ntu.hero.Hero
import com.ntu.hero.R
import com.ntu.hero.appt.appt_schedule.ApptScheActi
import com.ntu.hero.appt.appt_schedule.ApptScheActiInterface
import com.ntu.hero.databinding.ApptScheFrag1ConfirmBinding
import com.ntu.hero.global.GlobalVars
import com.ntu.hero.sql.entity.Appointment
import java.util.*

class ApptScheFrag1Confirm : Fragment() {
    private lateinit var binding: ApptScheFrag1ConfirmBinding

    // obs
    val obsDateStr = ObservableField<String>()
    val obsTimeStr = ObservableField<String>()
    val obsAddress = ObservableField<String>()
    val obsLat = ObservableField<String>()
    val obsLng = ObservableField<String>()
    val obsMapImgURL = ObservableField<String>()

    // for acti-frag comm
    private lateinit var fragActiInterface: ApptScheActiInterface

    override fun onAttach(context: Context) {
        super.onAttach(context)

        fragActiInterface = (context as ApptScheActi)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.appt_sche_frag1_confirm, container, false)
        binding.data = this
        binding.lifecycleOwner = this

        // set title
        fragActiInterface.setRoomTitle(getString(R.string.appointment))

        setupObs()

        return binding.root
    }

    //===== funcs
    private fun setupObs() {
        obsDateStr.set(arguments?.getString("dateStr"))
        obsTimeStr.set(arguments?.getString("timeStr"))

        // TEMP - hardcode address and coords for now
        obsAddress.set("69 Choa Chu Kang Loop #02-12, 689672, Singapore")

        obsLat.set("1.388414")
        obsLng.set("103.745233")
        obsMapImgURL.set("https://maps.googleapis.com/maps/api/staticmap?size=600x250&markers=color:red%7C${obsLat.get()},${obsLng.get()}&key=${GlobalVars.G_MAPS_API_KEY}")
    }


    //===== databinding funcs
    fun confirmBtnOnClick(_v: View) {
        // insert appt to sqlite then go back to main appt acti
        Hero.database!!.insertQuery().insertApptItem(
            Appointment(
                apptID = UUID.randomUUID().toString(),
                apptTitle = "Doctor Appointment",
                apptTimeStamp = System.currentTimeMillis(),
                apptDateTime = System.currentTimeMillis() + 3600,
                apptAddress = obsAddress.get(),
                apptDocImg = "",
                apptDocName = "Dr. Raymond Sheep",
                apptLat = obsLat.get(),
                apptLng = obsLng.get(),
                apptSrvName = fragActiInterface.getSrvName()
            )
        )

        fragActiInterface.finishActi()
    }

    fun addToCalBtnOnClick(_v: View) {

    }
}