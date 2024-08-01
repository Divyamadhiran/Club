package com.adv.ilook.view.ui.fragments.selectscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.adv.ilook.R
import com.adv.ilook.model.data.workflow.SelectScreenType
import com.adv.ilook.model.db.remote.repository.apprepo.CommonRepository
import com.adv.ilook.model.util.network.NetworkHelper
import com.adv.ilook.view.base.BaseViewModel
import com.adv.ilook.view.base.BasicFunction
import com.adv.ilook.view.ui.fragments.splash.TypeOfData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel

class SelectScreenViewModel @Inject
constructor(
    private val loginRepository: CommonRepository,
    private val networkHelper: NetworkHelper
): BaseViewModel(networkHelper) {

    private var selectScreenType by Delegates.notNull<SelectScreenType>()
    private val _nextScreenLiveData_1 = MutableLiveData<Int>()
    var nextScreenLiveData_1: LiveData<Int> = _nextScreenLiveData_1
    private val _nextScreenLiveData_2 = MutableLiveData<Int>()
    var nextScreenLiveData_2: LiveData<Int> = _nextScreenLiveData_2

    private val _prevScreenLiveData = MutableLiveData<Int>()
    var prevScreenLiveData: LiveData<Int> = _prevScreenLiveData
    override suspend fun init(function: (TypeOfData) -> Unit) {
       viewModelScope.launch {
            getWorkflowFromJson {
              launch(Dispatchers.Main) {
                  selectScreenType = it.screens?.selectScreenType!!
                  _nextScreenLiveData_1.postValue(BasicFunction.getScreens()[getNextScreen(0)] as Int)
                  _nextScreenLiveData_2.postValue(BasicFunction.getScreens()[getNextScreen(1)] as Int)
                  _prevScreenLiveData.postValue(BasicFunction.getScreens()[selectScreenType.previousScreen] as Int)
              }

            }
            withContext(Dispatchers.Main) {
                updateViewElements()
            }
        }
    }
    private fun updateViewElements() {
        val fileds= mutableMapOf<Int,String>()
        postIfEnabledAndNotEmpty( selectScreenType.views?.textView?.header?.enable, selectScreenType.views?.textView?.header?.text){
            fileds[R.id.header_instruct_title_TV] to it
        }
        postIfEnabledAndNotEmpty( selectScreenType.views?.buttonView?.guideBtn?.enable, selectScreenType.views?.buttonView?.guideBtn?.text){
            fileds[R.id.guide_mode_TV] to it
        }
        postIfEnabledAndNotEmpty( selectScreenType.views?.buttonView?.viBtn?.enable, selectScreenType.views?.buttonView?.viBtn?.text){
            fileds[R.id.visually_impaired_mode_TV] to it
        }
        _instructionWorkflow.postValue(fileds)
    }

    private fun getNextScreen(index:Int) =
        if (selectScreenType.nextScreen == "null") selectScreenType.selectScreen?.get(index)?.nextScreen
        else selectScreenType.nextScreen

    private val _instructionWorkflow=MutableLiveData<Map<Int,String>>()
    var instructionWorkflow:LiveData<Map<Int,String>> = _instructionWorkflow
}