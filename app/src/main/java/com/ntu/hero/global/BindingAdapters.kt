package com.ntu.hero.global

import android.util.Base64
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.ntu.hero.R


// glide binding adapter for profile imges (rounded corner)
@BindingAdapter("android:glidePropic")
fun glidePropic(view: ImageView, imgPath: String?) {
    if (imgPath == null) {
        return
    }

    // convert str to base64 if needed
    if (imgPath.contains(".jpg")) {
        // file, straight glide
        GlideApp.with(view)
            .load(imgPath)
            .placeholder(R.drawable.ic_profile_img)
            .centerCrop()
            .into(view)

    } else if (imgPath.contains("https://")) { // url
        GlideApp.with(view)
            .load(imgPath)
            .placeholder(R.drawable.ic_profile_img)
            .centerCrop()
            .into(view)

    } else { // base64 - encode
        GlideApp.with(view)
            .load(Base64.decode(imgPath, Base64.DEFAULT))
            .placeholder(R.drawable.ic_profile_img)
            .centerCrop()
            .into(view)

    }
}

// glide binding adapter for profile imges (circle)
@BindingAdapter("android:glidePropicCircle")
fun glidePropicCircle(view: ImageView, imgPath: String?) {
    if (imgPath == null) {
        return
    }

    // convert str to base64 if needed
    if (imgPath.contains(".jpg")) {
        // file, straight glide
        GlideApp.with(view)
            .load(imgPath)
            .placeholder(R.drawable.ic_profile_img)
            .circleCrop()
            .into(view)

    } else if (imgPath.contains("https://")) { // url
        GlideApp.with(view)
            .load(imgPath)
            .placeholder(R.drawable.ic_profile_img)
            .centerCrop()
            .into(view)

    } else {
        GlideApp.with(view)
            .load(Base64.decode(imgPath, Base64.DEFAULT))
            .placeholder(R.drawable.ic_profile_img)
            .circleCrop()
            .into(view)
    }
}

// glide binding adapter for normal imges
@BindingAdapter("android:glideImg")
fun glideImg(view: ImageView, imgPath: String?) {
    if (imgPath == null) {
        return
    }

    GlideApp.with(view)
        .load(imgPath)
        .placeholder(R.drawable.ic_logo)
        .error(R.drawable.ic_logo)
        .centerCrop()
        .into(view)
}

// on enter btn clicked softkeyboard
abstract class OnOkInSoftKeyboardListener {
    abstract fun onOkInSoftKeyboard()
}

@BindingAdapter("android:keyboardDoneBtn")
fun EditText.onEditorEnterAction(f: Function1<View?, Unit>?) {
    if (f == null) setOnEditorActionListener(null)
    else setOnEditorActionListener { v, actionId, event ->

        val imeAction = when (actionId) {
            EditorInfo.IME_ACTION_DONE,
            EditorInfo.IME_ACTION_SEND,
            EditorInfo.IME_ACTION_GO -> true
            else -> false
        }

        val keydownEvent = event?.keyCode == KeyEvent.KEYCODE_ENTER
                && event.action == KeyEvent.ACTION_DOWN

        if (imeAction or keydownEvent)
            true.also { f(v) }
        else false
    }
}