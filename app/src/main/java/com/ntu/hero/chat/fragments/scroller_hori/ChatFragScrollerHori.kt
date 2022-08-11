package com.ntu.hero.chat.fragments.scroller_hori

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.ntu.hero.R
import com.ntu.hero.chat.ChatActi
import com.ntu.hero.chat.ChatActiInterface
import com.ntu.hero.databinding.ChatFragScrollerHoriBinding

class ChatFragScrollerHori : Fragment() {
    private lateinit var binding: ChatFragScrollerHoriBinding

    val obsAdapter = ObservableField<ChatFragScrollerHoriAdapter>()
    val obsSelNumber = ObservableInt()

    // for rv
    private lateinit var rv: RecyclerView
    private lateinit var snapHelper: LinearSnapHelper

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
            DataBindingUtil.inflate(
                inflater,
                com.ntu.hero.R.layout.chat_frag_scroller_hori,
                container,
                false
            )
        binding.data = this
        binding.chatActi = context as ChatActi

        setupRV()

        return binding.root
    }

    //===== funcs
    private fun setupRV() {
        // set max range and initial val
        var startNumber = 20
        var minVal = 0
        var maxVal = 99

        when (arguments?.getString("type")) {
            context?.getString(R.string.ques_type_age) -> {
                startNumber = 20
                minVal = 0
                maxVal = 99
            }

            context?.getString(R.string.ques_type_scale) -> {
                startNumber = 5
                minVal = 0
                maxVal = 10
            }
        }

        // setup adapter based on max range
        obsAdapter.set(ChatFragScrollerHoriAdapter(frag = this, numbers = listOf(minVal..maxVal).flatten()))

        // setup snap to center for rv
        rv = binding.recyclerView

        snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(rv)

        // setup obs for getting snapped item
        rv.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val view = snapHelper.findSnapView(rv.layoutManager)!!
                obsSelNumber.set(rv.getChildAdapterPosition(view) - 1)
            }
        })

        // scroll and snap to start number
        snapToNumber(startNumber)
    }

    private fun snapToNumber(number: Int, needSmoothScroll: Boolean = false) {
//        if (needSmoothScroll) {
//            Log.d(GlobalVars.TAG1, "ChatFragScrollerHori, snapToNumber: number = $number")
//            rv.smoothScrollToPosition(number)
//
//        } else {
            rv.scrollToPosition(number)
            rv.post {
                val lm = rv.layoutManager!!
                val view = lm.findViewByPosition(number)
                if (view != null) {
                    val snapDistance = snapHelper.calculateDistanceToFinalSnap(lm, view)!!
                    if (snapDistance[0] != 0 || snapDistance[1] != 0) {
                        rv.scrollBy(snapDistance[0], snapDistance[1]);
                    }
                }
            }
//        }
    }

    //===== databinding funcs
    fun confirmOnClick(_v: View) {
        // post to retro for next question
        chatActiInterface.postAnswerForNextQues(listOf(
            obsSelNumber.get().toString() // send selected number to server
        ), null, true)
    }

    fun itemOnClick(number: Int) {
        // snap to number
        snapToNumber(number, true)
    }
}