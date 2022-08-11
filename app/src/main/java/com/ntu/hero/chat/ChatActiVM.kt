package com.ntu.hero.chat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ntu.hero.api.api_models.ChatItemModel
import com.ntu.hero.global.GlobalVars

class ChatActiVM : ViewModel() {
    var msgList: MutableLiveData<List<ChatItemModel>> = MutableLiveData()

    // for review
    private var backupList = listOf<ChatItemModel>()

    // functions for adding new item to existing mutable list
    fun addItemToMsgList(newListItem: ChatItemModel) {
        Log.d(GlobalVars.TAG1, "ChatActiVM, addItemToMsgList: newListItem = $newListItem")

        if (msgList.value == null) {
            msgList.postValue(
                mutableListOf(
                    ChatItemModel(isOutgoingInt = 1, nonBoldedText = ""),
                    newListItem
                )
            )
        } else {
            msgList.add(newListItem)
        }
    }

    fun <T> MutableLiveData<List<T>>.add(item: T) {
        val updatedItems = this.value as ArrayList
        updatedItems.add(item)
        this.value = updatedItems
    }

    // functions for adding new list of items to existing mutable list
    fun addListToMsgList(newList: List<ChatItemModel>) {
        Log.d(GlobalVars.TAG1, "ChatActiVM, addItemToMsgList: newList = $newList")

        if (msgList.value == null) {
            msgList.postValue(mutableListOf(ChatItemModel(isOutgoingInt = 1, nonBoldedText = "")))
        }
        msgList.addAll(newList)

    }

    fun <T> MutableLiveData<List<T>>.addAll(list: Collection<T>) {
        val updatedItems = this.value as ArrayList
        updatedItems.addAll(list)
        this.value = updatedItems
    }

    // backup current list and show new list (for reviewing)
    fun showNewList(newList: List<ChatItemModel>) {
        Log.d(GlobalVars.TAG1, "ChatActiVM, showNewList: newlist = $newList")

        // backup current msg list
        backupList = msgList.value!!

        // set new items to msgList
        msgList.postValue(newList)
    }

    // restore original list with new edited item
    fun restoreOriList() {
        msgList.postValue(backupList)
    }


}