package com.ntu.hero.chat.fragments.circles_hori

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ntu.hero.R
import com.ntu.hero.api.api_models.AnswerModel
import com.ntu.hero.chat.fragments.btns_verti.ChatFragBtnsVerti
import com.ntu.hero.databinding.ChatFragBtnsVertiItemBinding

class ChatFragBtnsVertiAdapter(private val frag: ChatFragBtnsVerti, private val list: List<AnswerModel>) : RecyclerView.Adapter<ChatFragBtnsVertiAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder((DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.chat_frag_btns_verti_item, parent, false) as ChatFragBtnsVertiItemBinding?)!!)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setData(frag, list[position])
    }

    class Holder internal constructor(internal var itemBinding: ChatFragBtnsVertiItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        private val context: Context

        init {
            context = itemView.context
        }

        fun setData(frag: ChatFragBtnsVerti, item: AnswerModel) {
            itemBinding.data = item

            itemView.setOnClickListener {
                frag.itemOnClick(item)
            }
        }
    }
}
