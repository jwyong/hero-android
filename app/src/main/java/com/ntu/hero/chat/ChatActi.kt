package com.ntu.hero.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ntu.hero.R
import com.ntu.hero.api.api_clients.RetroAPIClient
import com.ntu.hero.api.api_models.AnswerModel
import com.ntu.hero.api.api_models.ChatItemModel
import com.ntu.hero.api.api_models.ChatRoomModel
import com.ntu.hero.api.api_models.ChatRoomReviewModel
import com.ntu.hero.chat.chat_edit_acti.ChatEditActi
import com.ntu.hero.chat.fragments.circles_hori.ChatActiAdapter
import com.ntu.hero.databinding.ChatActiBinding
import com.ntu.hero.global.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActi : BaseActivity(), ChatActiInterface {
    lateinit var binding: ChatActiBinding

    private var btmBarFrag: Fragment? = null
    private val uiHelper = UIHelper()
    private lateinit var chatActiHelper: ChatActiHelper
    private val miscHelper = MiscHelper()

    // for http
    private val api = RetroAPIClient.api
    private var sessionID: String? = null
    private var prevQuestionUID: String? = null
    private var questionUID: String? = null
    private var answerList: List<String>? = null
    private var uuid: String? = null
    private var isReviewHttp: Boolean = false

    // for frag ansModelList
    private var ansModelList: List<AnswerModel>? = null

    // rv
    private lateinit var chatActiVM: ChatActiVM
    private lateinit var llm: LinearLayoutManager
    private val chatActiAdapter = ChatActiAdapter()
    private lateinit var scrollHelper: RecyclerViewHelper
    private lateinit var onScrollListener: RecyclerView.OnScrollListener

    // obs
    val obsPercent = ObservableInt(0)
    val obsBgColour = ObservableInt()
    val obsTextColour = ObservableInt()
    val obsProfileImg = ObservableField<String>(MPreferences.getProfileImg())

    // obs: for editing
    val obsGotEditOverlay = ObservableBoolean(false) // overaly is showing
    val obsEditingItem = ObservableField<ChatItemModel>()
    private lateinit var selectedView: View

    // bool for back pressed (exit chat room)
    private var gotExitChatItem = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set theme colour first
        val gotMainTheme = setupMainTheme()

        // setup main layout
        binding = DataBindingUtil.setContentView(this, R.layout.chat_acti)
        binding.data = this

        setupHelpers()

        // setup toolbar
        setupBackText()

        // setup btm bar frag
        btmBarFrag = supportFragmentManager.findFragmentById(com.ntu.hero.R.id.btm_frag)

        // setup RV
        setupRV()
        setupLiveData()

        // setup scrolling functions
        scrollHelper = RecyclerViewHelper()
        scrollHelper.setupScrollUX(binding.recyclerView, llm)
        scrollHelper.detectKeyboard(binding.root)

        // check if got theme color
        if (gotMainTheme) { // got main theme, straight get initial questions
            // add welcome back first
            chatActiVM.addItemToMsgList(
                ChatItemModel(
                    isOutgoingInt = 0,
                    nonBoldedText = GlobalVars.chat_start
                )
            )

            // retro get initial question
            retroGetInitialQuestions()

        } else { // no main theme, ask
            askMainTheme()
        }
    }

    //===== databinding funcs
    // cancel is editing
    fun cancelEditing(_v: View?) {
        obsGotEditOverlay.set(false)
    }

    // go to edit item acti
    fun editBtnOnclick(_v: View) {
        // remove editing overlay first
        obsGotEditOverlay.set(false)

        val intent = Intent(this, ChatEditActi::class.java)

        // get info of editing item
        val editingItem = obsEditingItem.get()!!
        val questionUID = editingItem.questionUID
        val uuid = editingItem.uuid
        val question = editingItem.nonBoldedText
        val answer = editingItem.boldedText

        intent.putExtra("question", question)
        intent.putExtra("answer", answer)
        intent.putExtra("questionUID", questionUID)
        intent.putExtra("uuid", uuid)
        startActivity(intent)
    }

    // chat item on click
    fun chatItemOnClick(v: View, item: ChatItemModel, position: Int) {
        // only do for editable items
        if (item.isEditable!!) {
            // update class vars first
            obsEditingItem.set(item)
            selectedView = v

            // check if item is off screen first
            val lastVisPos = llm.findLastCompletelyVisibleItemPosition()
            if (position > lastVisPos) { // smooth scroll to position first
                // add on scroll listener to rv
                val rv = binding.recyclerView
                rv.addOnScrollListener(onScrollListener)

                rv.post {
                    rv.smoothScrollToPosition(position)
                }
            } else { // straight do ui actions
                editingUIActions()
            }
        }
    }

    //===== funcs
    private fun setupHelpers() {
        chatActiHelper = ChatActiHelper(this)
    }

    private fun setupMainTheme(): Boolean {
        val bgColour = MPreferences.getIntValue(GlobalVars.PREF_CHAT_BG_COLOUR)

        // got select colour before
        if (bgColour != null && bgColour != -1) {
            obsBgColour.set(bgColour)

            val textColour = MPreferences.getIntValue(GlobalVars.PREF_CHAT_TEXT_COLOUR) ?: 0
            obsTextColour.set(textColour)

            return true
        } else { // default
            obsBgColour.set(ContextCompat.getColor(this, com.ntu.hero.R.color.primaryBG))
            obsTextColour.set(ContextCompat.getColor(this, com.ntu.hero.R.color.white))

            return false
        }
    }

    private fun setupRV() {
        binding.recyclerView.apply {
            llm = LinearLayoutManager(context)
            layoutManager = llm

            chatActiAdapter.setHasStableIds(true)
            adapter = chatActiAdapter
        }

        // setup onscroll listener first (for edit item)
        onScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        // continue actions for edit items here
                        editingUIActions()

                        // remove after done
                        recyclerView.removeOnScrollListener(this)
                    }
                }
            }
        }
    }

    private fun setupLiveData() {
        chatActiVM = ViewModelProviders.of(this).get(ChatActiVM::class.java)
        chatActiVM.msgList.observe(this, Observer { t ->
            chatActiAdapter.submitList(t)

            scrollHelper.scrollToBtm(t.size)
        })
    }

    // ask user for main theme
    private fun askMainTheme() {
        // add ask main theme msges (displayName dynamic)
        addToMsgList(
            ChatItemModel(
                isOutgoingInt = 0,
                nonBoldedText = GlobalVars.chat_setup_theme_1
            )
        )

        // navigate to hori btns
        val b = Bundle()
        b.putString("btn1Text", getString(com.ntu.hero.R.string.yes_please))
        b.putString("btn2Text", getString(com.ntu.hero.R.string.no_thanks))
        b.putInt("actionInt", 1) // action for selecting colour (1)
        navigateTo(com.ntu.hero.R.id.action_to_chatFragBtnsHori, b)
    }

    // ui actions for editing
    private fun editingUIActions() {
        // update chat acti obs
        obsGotEditOverlay.set(true)

        // get clicked item position
        val originalPos = IntArray(2)
        selectedView.getLocationOnScreen(originalPos)
        val x = originalPos[0]
        val y = originalPos[1]

        // get status bar height
        val statusBarHeight = uiHelper.getStatusBarHeight(this)

        // set to top margin
        val editingItem = binding.llChatBbl
        val param = editingItem.layoutParams as ConstraintLayout.LayoutParams
        param.topMargin = y - statusBarHeight
        editingItem.layoutParams = param
    }

    // combine items in list to str with commas as separation
    private fun concatListToStr(list: List<String>): String {
        var ansStr = ""
        list.forEachIndexed { index, it ->
            ansStr = if (index == 0) {
                it

            } else {
                "$it, $ansStr"
            }
        }

        return ansStr
    }


    //===== interface funcs
    override fun postAnswerForNextQues(
        answerList: List<String>,
        displayList: List<String>?,
        needAddMsg: Boolean
    ) {
        // go to empty frag first
        navigateTo(R.id.action_to_emptyFrag, null)

        // add items for display - ONLY if needed
        if (needAddMsg) {
            val msgList: List<String>
            if (displayList == null) {
                msgList = answerList
            } else {
                msgList = displayList
            }

            // create single item based on ansList
            chatActiVM.addItemToMsgList(
                ChatItemModel(
                    isOutgoingInt = 1,
                    questionUID = questionUID,
                    nonBoldedText = concatListToStr(msgList)
                )
            )
        }

        // create request body
        val chatRoomModel = ChatRoomModel(
            sessionID = sessionID,
            questionUID = questionUID,
            answerList = answerList,
            uuid = uuid
        )

        // do retro post
        retroGetNextQuestion(chatRoomModel)
    }

    // for getting next question - retry, color setup
    override fun getInitialOrNextQues(needAddMsg: Boolean) {
        // go to empty frag first
        navigateTo(R.id.action_to_emptyFrag, null)

        // post to initial or next question based on sesion id
        if (answerList == null) { // initial
            retroGetInitialQuestions()

        } else { // next
            if (isReviewHttp) {
                retroReview()

            } else {
                postAnswerForNextQues(answerList!!, null, needAddMsg)

            }
        }
    }

    // for getting previous question - back pressed
    override fun getInitialOrPrevQues() {
        // go to empty frag first
        navigateTo(R.id.action_to_emptyFrag, null)

        // post to initial or next question based on sesion id
        if (answerList == null) { // initial
            if (!setupMainTheme()) { // hvn't setup main theme
                askMainTheme()
            } else { // already setup
                retroGetInitialQuestions()
            }

        } else { // resume questions - use previous questionUID
            if (isReviewHttp) {
                retroReview()

            } else {
                retroGetNextQuestion(
                    ChatRoomModel(
                        sessionID = sessionID,
                        questionUID = prevQuestionUID,
                        answerList = answerList,
                        uuid = uuid
                    )
                )
            }
        }
    }

    override fun navigateTo(actionID: Int, bundle: Bundle?) {
        Navigation.findNavController(btmBarFrag?.view!!).navigate(actionID, bundle)

        if (chatActiVM.msgList.value != null) {
            scrollHelper.scrollToBtm(chatActiVM.msgList.value!!.size - 1)
        }

        // scroll rv to btm
//        if (chatActiVM.msgList.value != null) {
//            val rv = binding.recyclerView
//            rv.post {
//                rv.smoothScrollToPosition(chatActiVM.msgList.value!!.size - 1)
//            }
//        }
    }

    override fun setThemeColour(bgColour: Int, textColor: Int) {
        obsBgColour.set(bgColour)
        obsTextColour.set(textColor)
    }

    override fun addToMsgList(msg: ChatItemModel) {
        chatActiVM.addItemToMsgList(msg)
    }

    override fun resetBackBtn() {
        gotExitChatItem = false
    }

    override fun finishActi() {
        finish()
    }

    override fun updateIsEditing(isEditing: Boolean) {
        obsGotEditOverlay.set(isEditing)
    }

    override fun setAnsModelList(newlist: List<AnswerModel>) {
        ansModelList = newlist
    }

    override fun getAnsModelList(): List<AnswerModel> {
        return ansModelList!!
    }

    //===== http funcs
    private fun retroGetInitialQuestions() {
        Log.d(GlobalVars.TAG1, "ChatActi: retroGetInitialQuestions ")

        // header
        val header = MPreferences.getAccessToken()!!
        api.getInitialQuestions(header).enqueue(object :
            Callback<List<ChatRoomModel>> {
            override fun onResponse(
                call: Call<List<ChatRoomModel>>,
                response: Response<List<ChatRoomModel>>
            ) {
                if (!response.isSuccessful) {
                    retroRespUnsuc(response)
                    return
                }
                retroQuesRespSuccess(response)
            }

            override fun onFailure(call: Call<List<ChatRoomModel>>, t: Throwable) {
                retroFailure(t)
            }
        })
    }

    private fun retroGetNextQuestion(chatRoomModel: ChatRoomModel) {
        Log.d(GlobalVars.TAG1, "ChatActi: retroGetNextQuestion model = $chatRoomModel")

        // set global vars first for resume posting
        sessionID = chatRoomModel.sessionID
        questionUID = chatRoomModel.questionUID
        answerList = chatRoomModel.answerList
        uuid = chatRoomModel.uuid

        // start posting
        val header = MPreferences.getAccessToken()!!
        api.postAnswer(
            header = header,
            requestBody = chatRoomModel
        ).enqueue(object :
            Callback<List<ChatRoomModel>> {
            override fun onResponse(
                call: Call<List<ChatRoomModel>>,
                response: Response<List<ChatRoomModel>>
            ) {
                if (!response.isSuccessful) {
                    retroRespUnsuc(response)
                    return
                }
                retroQuesRespSuccess(response)
            }

            override fun onFailure(call: Call<List<ChatRoomModel>>, t: Throwable) {
                retroFailure(t)
            }
        })
    }

    // get all ques/ans for review
    override fun retroReview() {
        Log.d(GlobalVars.TAG1, "ChatActi: retroReview ")

        // set global vars first for resume posting
        isReviewHttp = true

        // start posting
        val header = MPreferences.getAccessToken()!!
        api.postToReview(
            header = header,
            requestBody = ChatRoomReviewModel(sessionID = sessionID)
        ).enqueue(object :
            Callback<List<ChatRoomReviewModel>> {
            override fun onResponse(
                call: Call<List<ChatRoomReviewModel>>,
                response: Response<List<ChatRoomReviewModel>>
            ) {
                if (!response.isSuccessful) {
                    retroRespUnsuc(response)
                    return
                }
                retroReviewRespSuccess(response)
            }

            override fun onFailure(call: Call<List<ChatRoomReviewModel>>, t: Throwable) {
                retroFailure(t)
            }
        })
    }

    //===== retro responses
    // response for all single questions related https
    private fun retroQuesRespSuccess(response: Response<List<ChatRoomModel>>) {
        // only populate items if no back btn items
        if (gotExitChatItem) {
            return
        }

        val respList = response.body()!!
        Log.d(GlobalVars.TAG1, "ChatActi: retroQuesRespSuccess respList = $respList")

        // save session id and question uid first
        sessionID = respList.last().sessionID
        prevQuestionUID = respList.last().prevQuesUID
        questionUID = respList.last().questionUID

        // set progress percentage
        obsPercent.set(respList.last().percentage)

        // navigate to answer frag based on resp question type
        chatActiHelper.navigateToFrag(respList.last())

        // map resp list to msgList
        val msgList = respList.map {
            ChatItemModel(
                isOutgoingInt = 0,
                questionUID = it.questionUID,
                nonBoldedText = it.questionBody!!
            )
        }

        // add to msgList in vm
        chatActiVM.addListToMsgList(msgList)
    }

    // review response - populate list of items
    private fun retroReviewRespSuccess(response: Response<List<ChatRoomReviewModel>>) {
        // only populate items if no back btn items
        if (gotExitChatItem) {
            return
        }

        val respList = response.body()!!
        Log.d(GlobalVars.TAG1, "ChatActi: retroReviewRespSuccess respList = $respList")

        // navigate to single btn for review
        val bundle = Bundle()
        bundle.putInt("actionInt", 1)
        bundle.putString("btnText", getString(R.string.yes_submit))
        navigateTo(R.id.action_to_chatFragSingleBtn, bundle)

        // map resp list to msgList
        val msgList = respList.map {
            // use answerCodes for answers if not null
            var ansList = it.answers
            if (ansList == null || ansList.isEmpty()) { // no answers, use answercodes
                ansList = it.answerCodes
            }

            ChatItemModel(
                isEditable = true,
                isOutgoingInt = 0,
                uuid = it.uuid,
                questionUID = it.questionUID,
                nonBoldedText = it.question!!,
                boldedText = concatListToStr(ansList!!) // answers with comma in it
            )
        }

        // add to msgList in vm
        chatActiVM.addListToMsgList(msgList)
    }

    private fun retroRespUnsuc(response: Response<*>) {
        // show retry frag
        val b = Bundle()
        b.putString("btnText", getString(R.string.chat_retry))
        b.putInt("actionInt", 2)
        navigateTo(R.id.action_to_chatFragSingleBtn, b)

        miscHelper.handleUnsuccError(this, "retroChatQuestions", response)
    }

    private fun retroFailure(throwable: Throwable) {
        // show retry frag
        val b = Bundle()
        b.putString("btnText", getString(R.string.chat_retry))
        b.putInt("actionInt", 2)
        navigateTo(R.id.action_to_chatFragSingleBtn, b)

        miscHelper.handleFailureError(this, "retroChatQuestions", throwable)
    }

    //===== override funcs
    override fun onBackPressed() {
        if (obsGotEditOverlay.get()) { // remove editing overlay if got
            obsGotEditOverlay.set(false)

        } else if (!gotExitChatItem) { // confirm user exit chat room
            // add exit confirmation msg
            addToMsgList(
                ChatItemModel(
                    nonBoldedText = getString(com.ntu.hero.R.string.chat_exit_hero_confirm),
                    isOutgoingInt = 0
                )
            )

            // navigate to 2 btns
            val bundle = Bundle()
            bundle.putString("btn1Text", getString(com.ntu.hero.R.string.yes_sure))
            bundle.putString("btn2Text", getString(com.ntu.hero.R.string.no_stay))
            bundle.putInt("actionInt", 0) // action for back
            navigateTo(com.ntu.hero.R.id.action_to_chatFragBtnsHori, bundle)

            // update to true
            gotExitChatItem = true
        }
    }
}

// interface for communication with fragments
interface ChatActiInterface {
    // for front-end init/next question
    fun getInitialOrNextQues(needAddMsg: Boolean)

    // for resuming questions - back btn or retry
    fun getInitialOrPrevQues()

    // navigate to fragment (for backbtn/non-posting navigation)
    fun navigateTo(actionID: Int, bundle: Bundle?)

    // post to chatbot
    fun postAnswerForNextQues(
        answerList: List<String>,
        displayList: List<String>?,
        needAddMsg: Boolean
    )

    // get review
    fun retroReview()

    // set theme colour
    fun setThemeColour(bgColour: Int, textColor: Int)

    // add item to msgList
    fun addToMsgList(msg: ChatItemModel)

    // finish acti
    fun finishActi()

    // update gotExitChatItem boolean for backpressed
    fun resetBackBtn()

    // update is editing (edit review)
    fun updateIsEditing(isEditing: Boolean)

    // set list to var for passing to next frag
    fun setAnsModelList(newlist: List<AnswerModel>)

    fun getAnsModelList(): List<AnswerModel>
}