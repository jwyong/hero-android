package com.ntu.hero.appt

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ntu.hero.Hero
import com.ntu.hero.sql.entity.Appointment

class ApptMainActiVM : ViewModel() {
    val apptList: LiveData<List<Appointment>> = Hero.database!!.selectQuery().getApptMainList()
}