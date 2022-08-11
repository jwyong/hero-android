package com.ntu.hero.registration.reg_0_on_board.fragments.items

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ntu.hero.R
import com.ntu.hero.databinding.Reg0OnBoardFrag0ItemBinding

class RegOnBoardFrag0Item(val imgResInt: Drawable, val title: String, val desc: String) : Fragment() {
    private lateinit var binding: Reg0OnBoardFrag0ItemBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.reg_0_on_board_frag_0_item, container, false)
        binding.data = this

        return binding.root
    }
}