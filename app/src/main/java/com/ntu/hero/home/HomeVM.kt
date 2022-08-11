package com.ntu.hero.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ntu.hero.Hero
import com.ntu.hero.sql.entity.HomeActivityList

class HomeVM : ViewModel() {

    val list: LiveData<List<HomeActivityList>> = Hero.database!!.selectQuery().getHomeActiList()


}