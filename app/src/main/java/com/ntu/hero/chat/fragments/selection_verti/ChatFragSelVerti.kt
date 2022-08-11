package com.ntu.hero.chat.fragments.selection_verti

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.ntu.hero.R
import com.ntu.hero.api.api_models.AnswerModel
import com.ntu.hero.chat.ChatActi
import com.ntu.hero.chat.ChatActiInterface
import com.ntu.hero.chat.fragments.circles_hori.ChatFragSelVertiAdapter
import com.ntu.hero.databinding.ChatFragSelVertiBinding
import com.ntu.hero.global.GlobalVars
import sg.com.agentapp.global.rv_selection.SelectionKeyProvider
import sg.com.agentapp.global.rv_selection.SelectionState

class ChatFragSelVerti : Fragment() {
    private lateinit var binding: ChatFragSelVertiBinding

    // for rv
    private lateinit var ansModelList: List<AnswerModel>
    private lateinit var rvAdapter: ChatFragSelVertiAdapter
    private lateinit var selTracker: SelectionTracker<Long>

    // for acti-frag comm
    private lateinit var chatActiInterface: ChatActiInterface

    override fun onAttach(context: Context) {
        super.onAttach(context)

        chatActiInterface = (context as ChatActi)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.chat_frag_sel_verti, container, false)
        binding.data = this
        binding.chatActi = context as ChatActi

        setupRV()
        setupRVSelTracker()

        return binding.root
    }

    private fun setupRV() {
        // get ans model list from args
        ansModelList = arguments!!.getSerializable("list") as List<AnswerModel>

        binding.recyclerView.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(context)
            rvAdapter = ChatFragSelVertiAdapter(this@ChatFragSelVerti, ansModelList)

            rvAdapter.setHasStableIds(true)

            adapter = rvAdapter
        }
    }


    //===== funcs
    private fun setupRVSelTracker() {
        val rv = binding.recyclerView

        // build tracker
        selTracker = SelectionTracker.Builder<Long>(
            "chatFragSelVertiSel",
            rv,
            SelectionKeyProvider(rv),
            ChatFragSelVertiItemsLookup(rv),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionState(true, true, true)
        ).build()

        rvAdapter.tracker = selTracker

        // add observer to tracker
        selTracker.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()

                    Log.d(GlobalVars.TAG1, "ChatFragSelVerti, onSelectionChanged: ")
                }
            })
    }


    //===== databinding funcs
    // holder onclick
    fun doneBtnOnClick(_v: View) {
        // get selection
        val sel = selTracker.selection

        // TEMP - log selections
        sel.forEach {
            Log.d(GlobalVars.TAG1, "ChatFragSelVerti, doneBtnOnClick: selAns = ${ansModelList[it.toInt()]}")
        }

        // post to retro for next question
        val ansIndexList = ansModelList.mapIndexedNotNull { index, it ->
            if (sel.contains(index.toLong())) {
                it.answerIndex
            } else {
                null
            }
        }
        val ansList = ansModelList.mapIndexedNotNull { index, it ->
            if (sel.contains(index.toLong())) {
                it.answer
            } else {
                null
            }
        }

        chatActiInterface.postAnswerForNextQues(
            ansIndexList, ansList, true
        )
    }
}