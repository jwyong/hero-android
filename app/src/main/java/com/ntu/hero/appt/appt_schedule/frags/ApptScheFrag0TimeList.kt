package com.ntu.hero.appt.appt_schedule.frags

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ntu.hero.R
import com.ntu.hero.api.api_models.ScheItemModel
import com.ntu.hero.appt.appt_schedule.ApptScheActi
import com.ntu.hero.appt.appt_schedule.ApptScheActiInterface
import com.ntu.hero.appt.appt_schedule.ApptScheActiVM
import com.ntu.hero.chat.fragments.circles_hori.ApptScheActiAdapter
import com.ntu.hero.databinding.ApptScheFrag0TimeListBinding

class ApptScheFrag0TimeList : Fragment() {
    private lateinit var binding: ApptScheFrag0TimeListBinding
    private var mView: View? = null

    // rv
    private val mAdapter = ApptScheActiAdapter(this)
    private lateinit var vm: ApptScheActiVM

    // for acti-frag comm
    lateinit var fragActiInterface: ApptScheActiInterface

    override fun onAttach(context: Context) {
        super.onAttach(context)

        fragActiInterface = (context as ApptScheActi)
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
                    R.layout.appt_sche_frag0_time_list,
                    container,
                    false
                )
            binding.data = this
            binding.lifecycleOwner = this

            // set title
            fragActiInterface.setRoomTitle(fragActiInterface.getSrvName() + " " + getString(R.string.appointments))

            // setup rv
            setupRV()
            setupLiveData()

            // post to get available times
            getAvailTimes()

            mView = binding.root
        }

        return mView
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
        vm = ViewModelProviders.of(this).get(ApptScheActiVM::class.java)
        vm.list.observe(this, Observer { t ->
            mAdapter.submitList(t)
        })
    }

    private fun getAvailTimes() {
        // TEMP - just add to list
        vm.list.postValue(
            listOf(
                ScheItemModel(
                    scheDate = "Monday, July 1, 2019",
                    scheItemTime = listOf("10:00 - 11:00", "11:00 - 12:00", "12:00 - 13:00")
                ),
                ScheItemModel(
                    scheDate = "Tuesday, July 2, 2019",
                    scheItemTime = listOf("10:00 - 11:00", "11:30 - 12:30", "12:40 - 13:50")
                ),
                ScheItemModel(
                    scheDate = "Wednesday, July 3, 2019",
                    scheItemTime = listOf("10:00 - 11:00", "11:30 - 12:30", "12:40 - 13:50")
                ),
                ScheItemModel(
                    scheDate = "Monday, July 1, 2019",
                    scheItemTime = listOf("10:00 - 11:00", "11:00 - 12:00", "12:00 - 13:00")
                ),
                ScheItemModel(
                    scheDate = "Tuesday, July 2, 2019",
                    scheItemTime = listOf("10:00 - 11:00", "11:30 - 12:30", "12:40 - 13:50")
                ),
                ScheItemModel(
                    scheDate = "Wednesday, July 3, 2019",
                    scheItemTime = listOf("10:00 - 11:00", "11:30 - 12:30", "12:40 - 13:50")
                ),
                ScheItemModel(
                    scheDate = "Monday, July 1, 2019",
                    scheItemTime = listOf("10:00 - 11:00", "11:00 - 12:00", "12:00 - 13:00")
                ),
                ScheItemModel(
                    scheDate = "Tuesday, July 2, 2019",
                    scheItemTime = listOf("10:00 - 11:00", "11:30 - 12:30", "12:40 - 13:50")
                ),
                ScheItemModel(
                    scheDate = "Wednesday, July 3, 2019",
                    scheItemTime = listOf("10:00 - 11:00", "11:30 - 12:30", "12:40 - 13:50")
                )
            )
        )
    }
}