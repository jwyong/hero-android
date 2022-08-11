package com.ntu.hero.chat.fragments.circles_hori

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ntu.hero.appt.ApptMainActi
import com.ntu.hero.databinding.ApptUpcomingItemBinding
import com.ntu.hero.sql.entity.Appointment


class ApptMainActiAdapter : ListAdapter<Appointment, ApptMainActiAdapter.Holder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            (DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                com.ntu.hero.R.layout.appt_upcoming_item,
                parent,
                false
            ) as ApptUpcomingItemBinding?)!!
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setData(getItem(position))
    }

    class Holder : RecyclerView.ViewHolder {
        private lateinit var itemBinding: ApptUpcomingItemBinding
        private val mainActi = itemView.context as ApptMainActi

        internal constructor(itemView: View) : super(itemView)

        constructor(inflate: ApptUpcomingItemBinding) : super(inflate.root) {
            itemBinding = inflate
        }

        fun setData(item: Appointment) {
            itemBinding.data = item
            itemBinding.executePendingBindings()

            // show appt dets in main acti on click
            itemView.setOnClickListener {
                // show appt det popup
                mainActi.showApptDetpopup(item)
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).apptRow!!.toLong()
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Appointment>() {
            override fun areItemsTheSame(
                oldUser: Appointment, newUser: Appointment
            ): Boolean {
                // User properties may have changed if reloaded from the DB, but ID is fixed
                return oldUser.apptRow!!.equals(newUser.apptRow)
            }

            override fun areContentsTheSame(
                oldUser: Appointment, newUser: Appointment
            ): Boolean {
                // NOTE: if you use equals, your object must properly override Object#equals()
                // Incorrectly returning false here will result in too many animations.
                return oldUser.equals(newUser)
            }
        }
    }
}
