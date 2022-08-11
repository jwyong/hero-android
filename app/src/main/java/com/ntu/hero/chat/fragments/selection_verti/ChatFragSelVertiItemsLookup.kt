package com.ntu.hero.chat.fragments.selection_verti

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.ntu.hero.chat.fragments.circles_hori.ChatFragSelVertiAdapter

class ChatFragSelVertiItemsLookup(private val recyclerView: RecyclerView) :
        ItemDetailsLookup<Long>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as ChatFragSelVertiAdapter.Holder)
                    .getItemDetails()
        }
        return null
    }
}
