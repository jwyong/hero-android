package com.ntu.hero.chat.fragments.circles_hori

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ntu.hero.R
import com.ntu.hero.appt.appt_schedule.frags.ApptScheFrag0TimeList
import com.ntu.hero.databinding.ApptScheFrag0ItemTimeBinding


class ApptScheTimeAdapter(private val frag: ApptScheFrag0TimeList, private val dateStr: String, private val list: List<String>) : RecyclerView.Adapter<ApptScheTimeAdapter.Holder>() {
    override fun getItemCount(): Int {
        return list.size;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            (DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                com.ntu.hero.R.layout.appt_sche_frag0_item_time,
                parent,
                false
            ) as ApptScheFrag0ItemTimeBinding?)!!
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setData(frag, dateStr, list[position])
    }

    class Holder : RecyclerView.ViewHolder {
        private lateinit var itemBinding: ApptScheFrag0ItemTimeBinding

        internal constructor(itemView: View) : super(itemView)

        constructor(inflate: ApptScheFrag0ItemTimeBinding) : super(inflate.root) {
            itemBinding = inflate
        }

        fun setData(frag: ApptScheFrag0TimeList, dateStr: String, item: String) {
            itemBinding.data = item
            itemBinding.executePendingBindings()

            itemBinding.rlItemView.setOnClickListener {
                // go to appt creation frag
                val bundle = Bundle()
                bundle.putString("dateStr", dateStr)
                bundle.putString("timeStr", item)

                frag.fragActiInterface.navigateTo(R.id.action_to_apptScheFrag1Confirm, bundle)
            }
        }
    }
}
