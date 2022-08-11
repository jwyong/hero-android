package com.ntu.hero.chat.fragments.circles_hori

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ntu.hero.api.api_models.ScheItemModel
import com.ntu.hero.appt.appt_schedule.frags.ApptScheFrag0TimeList
import com.ntu.hero.databinding.ApptScheFrag0ItemBinding
import com.ntu.hero.global.GuidToIdMap
import com.ntu.hero.global.SimpleDividerItemDecoration


class ApptScheActiAdapter(private val frag: ApptScheFrag0TimeList) : ListAdapter<ScheItemModel, ApptScheActiAdapter.Holder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            (DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                com.ntu.hero.R.layout.appt_sche_frag0_item,
                parent,
                false
            ) as ApptScheFrag0ItemBinding?)!!
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setData(frag, getItem(position))
    }

    class Holder : RecyclerView.ViewHolder {
        private lateinit var itemBinding: ApptScheFrag0ItemBinding

        internal constructor(itemView: View) : super(itemView)

        constructor(inflate: ApptScheFrag0ItemBinding) : super(inflate.root) {
            itemBinding = inflate
        }

        fun setData(frag: ApptScheFrag0TimeList, item: ScheItemModel) {
            itemBinding.data = item
            itemBinding.executePendingBindings()

            itemBinding.adapter = ApptScheTimeAdapter(frag, item.scheDate!!, item.scheItemTime)
            itemBinding.recyclerView.addItemDecoration(
                SimpleDividerItemDecoration(itemView.context)
            )
        }
    }

    // for stable ids
    private val guidToIdMap = GuidToIdMap()

    override fun getItemId(position: Int): Long {
        val guid = this.getItem(position).scheDate!!
        return guidToIdMap.getIdByGuid(guid)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ScheItemModel>() {
            override fun areItemsTheSame(
                oldUser: ScheItemModel, newUser: ScheItemModel
            ): Boolean {
                // User properties may have changed if reloaded from the DB, but ID is fixed
                return oldUser.scheDate!!.equals(newUser.scheDate)
            }

            override fun areContentsTheSame(
                oldUser: ScheItemModel, newUser: ScheItemModel
            ): Boolean {
                // NOTE: if you use equals, your object must properly override Object#equals()
                // Incorrectly returning false here will result in too many animations.
                return oldUser.equals(newUser)
            }
        }
    }
}
