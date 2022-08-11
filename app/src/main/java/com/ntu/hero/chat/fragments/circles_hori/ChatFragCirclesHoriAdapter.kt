package com.ntu.hero.chat.fragments.circles_hori

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ntu.hero.R
import com.ntu.hero.databinding.ChatFragCirclesHoriItemBinding

class ChatFragCirclesHoriAdapter(
    private val frag: ChatFragCirclesHori,
    private val btns: List<Int>
) : RecyclerView.Adapter<ChatFragCirclesHoriAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            (DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.chat_frag_circles_hori_item,
                parent,
                false
            ) as ChatFragCirclesHoriItemBinding?)!!
        )
    }

    override fun getItemCount(): Int {
        return btns.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setData(frag, btns[position])
    }

    class Holder internal constructor(internal var itemBinding: ChatFragCirclesHoriItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun setData(frag: ChatFragCirclesHori, colour: Int) {
            itemBinding.data = ContextCompat.getColor(itemView.context, colour)
            itemBinding.frag = frag
        }
    }
}
