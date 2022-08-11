package com.ntu.hero.global

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.*


class UIHelper {
    var bodyTextInt: Int? = null

    // hide softkeyboard (activity)
    fun hideActiKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus

        if (view == null) {
            view = View(activity)
        }

        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // hide softkeyboard (fragment)
    fun hideFragKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // get status bar height
    fun getStatusBarHeight(activity: Activity): Int {
        val rectangle = Rect()
        val window = activity.window
        window.decorView.getWindowVisibleDisplayFrame(rectangle)
        val statusBarHeight = rectangle.top

        return statusBarHeight
//        val contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop()
//        val titleBarHeight = contentViewTop - statusBarHeight

    }

    // convert date long to str (show time for today)
    fun dateLongToStrToday(dateLong: Long): String {
        val date = Date(dateLong)
        val format: SimpleDateFormat

        // check if today
        if (DateUtils.isToday(dateLong)) { // today - only show time (3:15pm)
            format = SimpleDateFormat("H:mma", Locale.getDefault())
        } else { // not today, show date
            format = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        }

        return format.format(date)

    }

    fun formatDateFromLong(dateLong: Long, formatStr: String): String {
        val date = Date(dateLong)
        val format = SimpleDateFormat(formatStr, Locale.getDefault())

        return format.format(date)

    }

    // popup with 2 btns
    fun dialog2btn(
        context: Context,
        message: Int,
        positiveText: Int?,
        negativeText: Int?,
        positiveFunc: Runnable,
        negativeFunc: Runnable?,
        isCancellable: Boolean
    ): Dialog {

        val dialog = Dialog(context)
        val dialogLayout = LayoutInflater.from(context)
        val dialogView = dialogLayout.inflate(com.ntu.hero.R.layout.dialog2btn, null)

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogView)
        dialog.setCancelable(isCancellable)
        dialog.create()

        val dialogMsg = dialog.findViewById<TextView>(com.ntu.hero.R.id.dialog_msg)
        val dialog_negative = dialog.findViewById<TextView>(com.ntu.hero.R.id.negative_button)
        val dialog_positive = dialog.findViewById<TextView>(com.ntu.hero.R.id.positive_button)

        dialogMsg.text = context.getString(message)

        //btn negative set function
        if (negativeText != null) {
            dialog_negative.text = context.getString(negativeText)
        }

        //btn positive set function
        if (positiveText != null) {
            dialog_positive.text = context.getString(positiveText)
        }

        //set OnclickListener
        dialog_negative.setOnClickListener {
            negativeFunc?.run()
            dialog.dismiss()
        }

        dialog_positive.setOnClickListener {
            positiveFunc.run()
            dialog.dismiss()
        }

        dialog.show()

        return dialog
    }

    // popup with 1 btn
    fun dialog1btn(
        context: Context,
        message: Int,
        positiveText: Int?,
        positiveFunc: Runnable?,
        isCancellable: Boolean
    ): Dialog {

        val dialog = Dialog(context)
        val dialogLayout = LayoutInflater.from(context)
        val dialogView = dialogLayout.inflate(com.ntu.hero.R.layout.dialog1btn, null)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogView)
        dialog.setCancelable(isCancellable)
        dialog.create()

        val dialogMsg = dialog.findViewById<TextView>(com.ntu.hero.R.id.dialog_msg)
        val dialog_positive = dialog.findViewById<TextView>(com.ntu.hero.R.id.positive_button)

        dialogMsg.text = context.getString(message)

        //btn positive set function
        if (positiveText != null) {
            dialog_positive.text = context.getString(positiveText)
        }

        dialog_positive.setOnClickListener {
            positiveFunc?.run()
            dialog.dismiss()
        }

        dialog.show()

        return dialog
    }

    // bottomsheet for fingerprint scanning
    fun btmDialogTouchID(
        context: Context,
        negativeFunc: Runnable?,
        isCancellable: Boolean
    ): BottomSheetDialog {
        val btmSheetDialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(com.ntu.hero.R.layout.btm_sheet_touch_id, null)

        btmSheetDialog.setContentView(view)
        btmSheetDialog.setCancelable(isCancellable)
        btmSheetDialog.create()
        btmSheetDialog.window?.findViewById<View>(com.ntu.hero.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)

        val cancelBtn = view.findViewById<Button>(com.ntu.hero.R.id.btn_cancel)
        cancelBtn.setOnClickListener {
            negativeFunc?.run()
            btmSheetDialog.dismiss()
        }

        btmSheetDialog.show()

        return btmSheetDialog
    }

    // btm sheet for 2 items
    fun btmDialog2Items(
        context: Context,
        btn1Text: Int,
        btn2Text: Int,
        btn1Func: Runnable,
        btn2Func: Runnable,
        isCancellable: Boolean
    ): BottomSheetDialog {
        val btmSheetDialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(com.ntu.hero.R.layout.btm_sheet_2_items, null)

        btmSheetDialog.setContentView(view)
        btmSheetDialog.setCancelable(isCancellable)
        btmSheetDialog.create()
        btmSheetDialog.window?.findViewById<View>(com.ntu.hero.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)

        val btn1 = view.findViewById<Button>(com.ntu.hero.R.id.btn_1)
        btn1.text = context.getString(btn1Text)
        btn1.setOnClickListener {
            btn1Func.run()
            btmSheetDialog.dismiss()
        }

        val btn2 = view.findViewById<Button>(com.ntu.hero.R.id.btn_2)
        btn2.text = context.getString(btn2Text)
        btn2.setOnClickListener {
            btn2Func.run()
            btmSheetDialog.dismiss()
        }

        btmSheetDialog.show()

        return btmSheetDialog
    }
}