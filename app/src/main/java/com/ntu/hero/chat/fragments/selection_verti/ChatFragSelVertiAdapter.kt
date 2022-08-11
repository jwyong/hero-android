package com.ntu.hero.chat.fragments.circles_hori

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.ntu.hero.R
import com.ntu.hero.api.api_models.AnswerModel
import com.ntu.hero.chat.ChatActi
import com.ntu.hero.chat.fragments.selection_verti.ChatFragSelVerti
import com.ntu.hero.databinding.ChatFragSelVertiItemBinding

class ChatFragSelVertiAdapter(
    private val frag: ChatFragSelVerti,
    private val answerList: List<AnswerModel>
) : RecyclerView.Adapter<ChatFragSelVertiAdapter.Holder>() {
    var tracker: SelectionTracker<Long>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            (DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.chat_frag_sel_verti_item,
                parent,
                false
            ) as ChatFragSelVertiItemBinding?)!!
        )
    }

    override fun getItemCount(): Int {
        return answerList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        tracker?.let {
            holder.setData(frag, answerList[position], it.isSelected(getItemId(position)))
        }
    }

    override fun getItemId(position: Int): Long {
        return answerList[position].answerIndex.toLong()
    }

    class Holder internal constructor(internal var itemBinding: ChatFragSelVertiItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        private val context: Context

        init {
            context = itemView.context
        }

        fun setData(frag: ChatFragSelVerti, item: AnswerModel, isActivated: Boolean) {
            itemBinding.data = item.answer
            itemBinding.chatActi = frag.context as ChatActi

            itemView.setOnClickListener {
//                Log.d(GlobalVars.TAG1, "Holder, setData: item = ${item.answer}")
            }

            // for selection
            itemView.isActivated = isActivated
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): Long? = itemId
                override fun inSelectionHotspot(e: MotionEvent): Boolean = true
            }
    }
}
