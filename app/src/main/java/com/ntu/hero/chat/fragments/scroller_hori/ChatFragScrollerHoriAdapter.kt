package com.ntu.hero.chat.fragments.scroller_hori

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ntu.hero.R
import com.ntu.hero.databinding.ChatFragScrollerHoriItemBinding

class ChatFragScrollerHoriAdapter(
    private val frag: ChatFragScrollerHori,
    private val numbers: List<Int> = listOf(0..99).flatten()
) : RecyclerView.Adapter<ChatFragScrollerHoriAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return if (viewType == 0) {
            // empty space
            Holder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.chat_scroller_empty_item,
                    parent,
                    false
                )
            )
        } else {
            Holder(
                (DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.chat_frag_scroller_hori_item,
                    parent,
                    false
                ) as ChatFragScrollerHoriItemBinding?)!!
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            0
        } else {
            1
        }
    }

    override fun getItemCount(): Int {
        return numbers.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setData(frag, numbers[position])
    }

    class Holder : RecyclerView.ViewHolder {
        private lateinit var itemBinding: ChatFragScrollerHoriItemBinding

        internal constructor(itemView: View) : super(itemView)

        constructor(inflate: ChatFragScrollerHoriItemBinding) : super(inflate.root) {
            itemBinding = inflate
        }

        fun setData(frag: ChatFragScrollerHori, number: Int) {
            if (itemViewType != 0) {
                itemBinding.data = number
                itemBinding.frag = frag
            }
        }
    }
}
