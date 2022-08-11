package com.ntu.hero.global

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ntu.hero.R


class RecyclerViewHelper {
    private lateinit var recyclerView: RecyclerView
    private lateinit var llm: LinearLayoutManager
    private var keyBoardScrolled: Boolean = false
    private var lastVisibleItem: Int = 0
    private var btmPosition: Int = 0

    // setup scroll listener and other related UX
    fun setupScrollUX(recyclerView: RecyclerView, rvLayoutManager: LinearLayoutManager) {
        this.recyclerView = recyclerView
        this.llm = rvLayoutManager

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                lastVisibleItem = rvLayoutManager.findLastCompletelyVisibleItemPosition()
            }
        })
    }

    // auto scroll when keyboard up
    fun detectKeyboard(rootView: View) {
        //set softkeyboard open/close listener
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)
            val screenHeight = rootView.rootView.height

            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            val keypadHeight = screenHeight - r.bottom

//            if (keypadHeight > 15) { // 0.15 ratio is perhaps enough to determine keypad height.
            if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                // keyboard is opened
                if (!keyBoardScrolled) { //scroll to position only once
                    scrollToBtm(btmPosition)

                    keyBoardScrolled = true
                }

            } else {
                if (keyBoardScrolled) { //scroll to position only once
                    //keyboard is closed
                    scrollToBtm(btmPosition)

                    keyBoardScrolled = false
                }
            }
        }
    }

    // scroll to btm if user hvnt scrolled
    fun scrollToBtm(btmPosition: Int) {
        Log.d(GlobalVars.TAG1, "RecyclerViewHelper, scrollToBtm: btmPost = $btmPosition ")
        this.btmPosition = btmPosition

//        llm.scrollToPositionWithOffset(btmPosition, 0)

//        if (lastVisibleItem == btmPosition) {
        recyclerView.post {
            recyclerView.smoothScrollToPosition(btmPosition)
        }
//        }
    }
}

class SimpleDividerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private val mDivider: Drawable = ContextCompat.getDrawable(context, R.drawable.line_divider)!!

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom = top + mDivider.intrinsicHeight

            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }
}


// for stable ids
class GuidToIdMap {
    private val guidToIdMapping = HashMap<String, Long>()
    private val idToGuidMapping = HashMap<Long, String>()

    private var idGenerator: Long = 0

    fun getIdByGuid(guid: String): Long {
        if (!guidToIdMapping.containsKey(guid)) {
            val id = idGenerator++
            guidToIdMapping[guid] = id
            idToGuidMapping[id] = guid
        }
        return guidToIdMapping[guid]!!
    }

    fun getGuidById(id: Long): String? {
        return if (idToGuidMapping.containsKey(id)) {
            idToGuidMapping[id]
        } else null
    }
}