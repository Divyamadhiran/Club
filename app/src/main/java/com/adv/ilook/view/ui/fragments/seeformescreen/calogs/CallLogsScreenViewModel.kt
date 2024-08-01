package com.adv.ilook.view.ui.fragments.seeformescreen.calogs

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adv.ilook.di.IoDispatcher
import com.adv.ilook.di.MainDispatcher
import com.adv.ilook.model.data.workflow.CallLogsScreen
import com.adv.ilook.model.data.workflow.HomeScreen
import com.adv.ilook.model.db.remote.repository.apprepo.SeeForMeRepo
import com.adv.ilook.model.util.network.NetworkHelper
import com.adv.ilook.view.base.BaseViewModel
import com.adv.ilook.view.ui.fragments.splash.TypeOfData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.properties.Delegates

private const val TAG = "==>>CallLogsScreenViewModel"
@HiltViewModel
class CallLogsScreenViewModel  @Inject constructor(
    private val loginRepository: SeeForMeRepo,
    private val networkHelper: NetworkHelper,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
):BaseViewModel(networkHelper)
{

    private var callLogsScreen by Delegates.notNull<CallLogsScreen>()
    private val _nextScreenLiveData = MutableLiveData<Int>()
    var nextScreenLiveData: LiveData<Int> = _nextScreenLiveData
    private val _prevScreenLiveData = MutableLiveData<Int>()
    var prevScreenLiveData: LiveData<Int> = _prevScreenLiveData


    override suspend fun init(function: (TypeOfData) -> Unit) {
        viewModelScope.launch {
            getWorkflowFromJson {
                launch {
                    callLogsScreen = it.screens?.callLogsScreen!!
                    Log.d(TAG, "init: prev ->${callLogsScreen.previousScreen.toString()}")
                    Log.d(TAG, "init: next ->${callLogsScreen.nextScreen.toString()}")
                    /*  Log.d(TAG, "init: nextSelect ->${getNextScreen(0)}")
                      _nextScreenLiveData_1.postValue(BasicFunction.getScreens()[getNextScreen(0)] as Int)
                      _prevScreenLiveData.postValue(BasicFunction.getScreens()[contactsScreen.previousScreen] as Int)
                      contactsScreen.selectScreen?.forEachIndexed { index, selectScreenItem ->
                          updateMap(index, selectScreenItem?.nextScreen as String)
                      }*/
                }

            }
            withContext(Dispatchers.Main) {

            }
        }
    }

}