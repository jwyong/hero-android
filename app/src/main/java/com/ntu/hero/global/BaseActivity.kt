package com.ntu.hero.global

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ntu.hero.R

/* Created by Soapp on 4 July 2017. */
open class BaseActivity : AppCompatActivity() {
    protected fun setupBackText() {
        val backText: TextView = findViewById(R.id.back_text)
        backText.setOnClickListener{
            onBackPressed()
        }
    }
}
