package com.adv.ilook.view.ui.fragments.seeforme

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adv.ilook.model.data.workflow.SeeForMeScreen
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

private const val TAG = "==>>SeeformeViewModel"
@HiltViewModel
class SeeformeScreenViewModel @Inject
constructor(
    private val loginRepository: CommonRepository,
    private val networkHelper: NetworkHelper
): BaseViewModel(networkHelper) {
    private var seeForMeScreen by Delegates.notNull<SeeForMeScreen>()

    private val _nextScreenLiveData_1 = MutableLiveData<Int>()
    var nextScreenLiveData_1: LiveData<Int> = _nextScreenLiveData_1
    private val _nextScreenLiveData_2 = MutableLiveData<Int>()
    var nextScreenLiveData_2: LiveData<Int> = _nextScreenLiveData_2
    private val _prevScreenLiveData = MutableLiveData<Int>()
    var prevScreenLiveData: LiveData<Int> = _prevScreenLiveData
    // Define a MutableLiveData property
    private val _allScreenLiveData = MutableLiveData<Map<Int, Any>>()
    // Expose the LiveData to observers
    val allScreenLiveData: LiveData<Map<Int, Any>> get() = _allScreenLiveData

    init {
        // Initialize the map with an empty map or any initial data
        _allScreenLiveData.value = emptyMap()
    }


    override suspend fun init(function: (TypeOfData) -> Unit) {
        viewModelScope.launch {
            getWorkflowFromJson {
                launch(Dispatchers.Main) {
                    seeForMeScreen = it.screens?.seeForMeScreen!!
                    Log.d(TAG, "init: prev ->${seeForMeScreen.previousScreen.toString()}")
                    Log.d(TAG, "init: next ->${seeForMeScreen.nextScreen.toString()}")
                    Log.d(TAG, "init: nextSelect ->${getNextScreen(0)}")
                    _nextScreenLiveData_1.postValue(BasicFunction.getScreens()[getNextScreen(0)] as Int)

                    _prevScreenLiveData.postValue(BasicFunction.getScreens()[seeForMeScreen.previousScreen] as Int)
                    seeForMeScreen.selectScreen?.forEachIndexed { index, selectScreenItem ->
                        updateMap(index, selectScreenItem?.nextScreen as String)
                    }


                }

            }
            withContext(Dispatchers.Main) {

            }
        }
    }

    private fun getNextScreen(index:Int) =
        if (seeForMeScreen.nextScreen == "null") seeForMeScreen.selectScreen?.get(index)?.nextScreen
        else seeForMeScreen.nextScreen
    // Function to update the map
    fun updateMap(key: Int, value: Any) {
        val currentMap = _allScreenLiveData.value ?: emptyMap()
        val updatedMap = currentMap.toMutableMap()
        updatedMap[key] = value
        _allScreenLiveData.value = updatedMap
    }

    // Function to remove an item from the map
    fun removeFromMap(key: Int) {
        val currentMap = _allScreenLiveData.value ?: emptyMap()
        val updatedMap = currentMap.toMutableMap()
        updatedMap.remove(key)
        _allScreenLiveData.value = updatedMap
    }
}