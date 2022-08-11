package com.ntu.hero.appt.appt_schedule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ntu.hero.api.api_models.ScheItemModel

class ApptScheActiVM : ViewModel() {
    var list: MutableLiveData<List<ScheItemModel>> = MutableLiveData()
}