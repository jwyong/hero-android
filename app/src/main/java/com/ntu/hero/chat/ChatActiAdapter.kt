package com.ntu.hero.chat.fragments.circles_hori

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ntu.hero.api.api_models.ChatItemModel
import com.ntu.hero.chat.ChatActi
import com.ntu.hero.databinding.ChatActiItemInBinding
import com.ntu.hero.databinding.ChatActiItemOutBinding
import com.ntu.hero.global.GlobalVars
import com.ntu.hero.global.GuidToIdMap
import kotlinx.android.synthetic.main.chat_acti_item_in.view.*


class ChatActiAdapter : ListAdapter<ChatItemModel, ChatActiAdapter.Holder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return when (viewType) {
            // empty space
            99 -> Holder(
                LayoutInflater.from(parent.context).inflate(
                    com.ntu.hero.R.layout.chat_acti_top_item,
                    parent,
                    false
                )
            )

            // in
            0 -> Holder(
                (DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    com.ntu.hero.R.layout.chat_acti_item_in,
                    parent,
                    false
                ) as ChatActiItemInBinding?)!!
            )

            // out
            1 -> Holder(
                (DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    com.ntu.hero.R.layout.chat_acti_item_out,
                    parent,
                    false
                ) as ChatActiItemOutBinding?)!!
            )

            else -> Holder(
                (DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    com.ntu.hero.R.layout.chat_acti_item_in,
                    parent,
                    false
                ) as ChatActiItemInBinding?)!!
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            99 // empty space on top
        } else {
            getItem(position).isOutgoingInt
        }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val currentItem = getItem(position)

        // show/hide profile img (incoming only)
        var showProfileImg = true
        if (currentItem.isOutgoingInt == 0) { // incoming
            if (position > 0) { // not first item, check
                val previousItem = getItem(position - 1)
                if (previousItem.isOutgoingInt == 0) { // last item also outgoing, hide
                    showProfileImg = false
                }
            }
        }

        holder.setData(getItem(position), showProfileImg)
    }

    // for stable ids
    private val guidToIdMap = GuidToIdMap()
    override fun getItemId(position: Int): Long {
        val guid = this.getItem(position).uuid?: position.toString()
        return guidToIdMap.getIdByGuid(guid)
    }

    class Holder : RecyclerView.ViewHolder {
        private val chatActi = itemView.context as ChatActi
        private lateinit var chatActiItemInBinding: ChatActiItemInBinding
        private lateinit var chatActiItemOutBinding: ChatActiItemOutBinding

        internal constructor(itemView: View) : super(itemView)

        constructor(inflate: ChatActiItemInBinding) : super(inflate.root) {
            chatActiItemInBinding = inflate
        }

        constructor(inflate: ChatActiItemOutBinding) : super(inflate.root) {
            chatActiItemOutBinding = inflate
        }

        fun setData(item: ChatItemModel, showProfileImg: Boolean) {
            when (itemViewType) {
                99 -> {
                } // no action for empty top view

                0 -> { // in
                    chatActiItemInBinding.data = item
                    chatActiItemInBinding.showProfileImg = showProfileImg
                    chatActiItemInBinding.chatActi = chatActi
                    chatActiItemInBinding.executePendingBindings()

                    itemView.ll_chat_bbl.setOnClickListener {
                        chatActi.chatItemOnClick(it, item, adapterPosition)
                        Log.d(GlobalVars.TAG1, "Holder, setData: uuid = ${item.uuid}")
                    }
                }

                1 -> { // out
                    chatActiItemOutBinding.data = item

                    // for theme colour and profile img
                    chatActiItemOutBinding.chatActi = chatActi
                    chatActiItemOutBinding.executePendingBindings()
                }

                else -> {
                    chatActiItemInBinding.data = item
                    chatActiItemInBinding.executePendingBindings()
                }
            }



            // set onclick on editable items
//            if (item.isEditable!!) {
//                itemView.setOnClickListener {
//                    // update chat acti obs
//                    chatActi.obsGotEditOverlay.set(true)
//                    chatActi.obsEditingItem.set(item)
//
//                    // get clicked item position
//                    val originalPos = IntArray(2)
////                    itemView.getLocationInWindow(originalPos) //or view.getLocationOnScreen(originalPos)
//                    itemView.getLocationOnScreen(originalPos)
//                    val x = originalPos[0]
//                    val y = originalPos[1]
//
//                    // get status bar height
//                    val statusBarHeight = uiHelper.getStatusBarHeight(chatActi)
//
//                    // set to top margin
//                    val editingItem = chatActi.binding.llChatBbl
//                    val param = editingItem.layoutParams as ConstraintLayout.LayoutParams
//                    param.topMargin = y - statusBarHeight
//                    editingItem.layoutParams = param
//
//                    Log.d(
//                        GlobalVars.TAG1,
//                        "Holder, setData: uid = ${item.uuid}, x = $x, y = $y"
//                    )
//                }
//            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ChatItemModel>() {
            override fun areItemsTheSame(
                oldUser: ChatItemModel, newUser: ChatItemModel
            ): Boolean {
                // User properties may have changed if reloaded from the DB, but ID is fixed
                return oldUser.uuid.equals(newUser.uuid)
            }

            override fun areContentsTheSame(
                oldUser: ChatItemModel, newUser: ChatItemModel
            ): Boolean {
                // NOTE: if you use equals, your object must properly override Object#equals()
                // Incorrectly returning false here will result in too many animations.
                return oldUser.equals(newUser)
            }
        }
    }
}
