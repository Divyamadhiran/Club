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
constructor(private val loginRepository: CommonRepository, private val networkHelper: NetworkHelper): BaseViewModel(networkHelper) {

    private var selectScreenType by Delegates.notNull<SelectScreenType>()
    private val _nextScreenLiveData_1 = MutableLiveData<Int>()
    var nextScreenLiveData_1: LiveData<Int> = _nextScreenLiveData_1
    private val _nextScreenLiveData_2 = MutableLiveData<Int>()
    var nextScreenLiveData_2: LiveData<Int> = _nextScreenLiveData_2

    private val _selectScrWorkflow = MutableLiveData<Map<Int, String>>()
    var selectScrWorkflow: LiveData<Map<Int, String>> = _selectScrWorkflow

    private val _selectScrFieldsEnable = MutableLiveData<Map<Int, Boolean>>()
    val selectScrFieldsEnable: LiveData<Map<Int, Boolean>> get() = _selectScrFieldsEnable
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
        val filedValues = mutableMapOf<Int, String>()
        val filedCondition = mutableMapOf<Int, Boolean>()
        selectScreenType.views?.let { view ->
            view.textView?.header.let { head ->
                head?.enable.let { enable ->
                    head?.text.let { text ->
                        postIfEnabledAndNotEmpty(enable, text) {
                            filedValues[R.id.header_title] = it
                        }
                    }
                }
            }




            view.buttonView.let { button ->
                button?.guideBtn.let { guideBtn ->
                    guideBtn?.enable.let { enable ->
                        guideBtn?.text.let { text ->
                            postIfEnabledAndNotEmpty(enable, text) {
                                filedValues[R.id.txt_guide_mode] = it
                            }
                        }
                    }
                }
            }
            view.buttonView.let { button ->
                button?.viBtn.let { guideBtn ->
                    guideBtn?.enable.let { enable ->
                        guideBtn?.text.let { text ->
                            postIfEnabledAndNotEmpty(enable, text) {
                                filedValues[R.id.txt_visually_impaired_mode] = it
                            }
                        }
                    }
                }
            }


        }




        selectScreenType.views?.let {view->
            view?.textView?.header?.enable?.let { enable->
                filedCondition[R.id.header_title] = enable
            }
        }


        selectScreenType.views?.let {view->
            view?.buttonView?.guideBtn?.enable?.let { enable->
                filedCondition[R.id.txt_guide_mode] = enable
            }
        }


        selectScreenType.views?.let {view->
            view?.buttonView?.viBtn?.enable?.let { enable->
                filedCondition[R.id.txt_visually_impaired_mode] = enable
            }
        }


        _selectScrWorkflow.postValue(filedValues)
        _selectScrFieldsEnable.postValue(filedCondition)
    }



    private fun getNextScreen(index:Int) =
        if (selectScreenType.nextScreen == "null") selectScreenType.selectScreen?.get(index)?.nextScreen
        else selectScreenType.nextScreen

    private val _instructionWorkflow=MutableLiveData<Map<Int,String>>()
    var instructionWorkflow:LiveData<Map<Int,String>> = _instructionWorkflow
}