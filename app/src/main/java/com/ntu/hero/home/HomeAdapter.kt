package com.ntu.hero.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ntu.hero.R
import com.ntu.hero.databinding.HomeActiItemBinding
import com.ntu.hero.sql.entity.HomeActivityList

class HomeAdapter : ListAdapter<HomeActivityList, HomeAdapter.Holder>(DIFF_CALLBACK) {
    // for rv selection
    var tracker: SelectionTracker<Long>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder((DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.home_acti_item, parent, false) as HomeActiItemBinding?)!!)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setData(getItem(position))
    }

    override fun getItemId(position: Int): Long = getItem(position).homeRow!!.toLong()

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HomeActivityList>() {
            override fun areItemsTheSame(
                    oldUser: HomeActivityList, newUser: HomeActivityList): Boolean {
                // User properties may have changed if reloaded from the DB, but ID is fixed
                //            return oldUser.getChatList().getChatJid().equals(newUser.getChatList().getChatJid());

                return oldUser.homeRow!!.equals(newUser.homeRow)
            }

            override fun areContentsTheSame(
                    oldUser: HomeActivityList, newUser: HomeActivityList): Boolean {
                // NOTE: if you use equals, your object must properly override Object#equals()
                // Incorrectly returning false here will result in too many animations.
                return oldUser.equals(newUser)
            }
        }
    }

    class Holder internal constructor(internal var itemBinding: HomeActiItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun setData(item: HomeActivityList) {
            itemBinding.data = item
        }
    }
}
