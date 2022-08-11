package com.ntu.hero.registration.reg_0_on_board.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.viewpager.widget.ViewPager
import com.ntu.hero.R
import com.ntu.hero.databinding.Reg0OnBoardFrag0Binding
import com.ntu.hero.registration.reg_0_on_board.RegOnBoardActi
import com.ntu.hero.registration.reg_0_on_board.fragments.items.RegOnBoardFrag0Adapter
import com.ntu.hero.registration.reg_0_on_board.fragments.items.RegOnBoardFrag0Item

class RegOnBoardFrag0 : Fragment() {
    private lateinit var binding: Reg0OnBoardFrag0Binding
    private lateinit var viewPager: ViewPager

    var mAdapter = ObservableField<RegOnBoardFrag0Adapter>()

    val obsDotBg = ObservableInt()
    val obsBtnTxt = ObservableField<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.reg_0_on_board_frag_0, container, false)
        binding.data = this

        // set view pager
        setupViewPager()

        // set observables
        obsDotBg.set(0)
        obsBtnTxt.set(getString(R.string.next))

        return binding.root
    }
    
    private fun setupViewPager() {
        // set view pager data
        val acti = context as RegOnBoardActi
        val adapter =
            RegOnBoardFrag0Adapter(
                acti.supportFragmentManager
            )

        adapter.addFragment(RegOnBoardFrag0Item(acti.getDrawable(R.drawable.onboarding_1)!!, getString(R.string.reg_0_0_title), getString(R.string.reg_0_0_desc)))
        adapter.addFragment(RegOnBoardFrag0Item(acti.getDrawable(R.drawable.onboarding_2)!!, getString(R.string.reg_0_1_title), getString(R.string.reg_0_1_desc)))
        adapter.addFragment(RegOnBoardFrag0Item(acti.getDrawable(R.drawable.onboarding_3)!!, getString(R.string.reg_0_2_title), getString(R.string.reg_0_2_desc)))

        mAdapter.set(adapter)

        // set listener
        viewPager = binding.viewPager
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                obsDotBg.set(position)

                // set btn text if reach last page
                if (position == 2) {
                    obsBtnTxt.set(getString(R.string.get_started))
                } else {
                    obsBtnTxt.set(getString(R.string.next))
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    //===== databinding funcs
    fun nextBtn(v: View) {
        when (obsDotBg.get()) {
            0, 1 -> {
                viewPager.setCurrentItem(obsDotBg.get() + 1, true)
            }

            2 -> { // check permissions
                Navigation.findNavController(v).navigate(R.id.action_regOnBoardFrag0_to_regOnBoardFrag1)

//                val intent = Intent(this, HomeActi::class.java)
//                startActivity(intent)
            }
        }
    }
}