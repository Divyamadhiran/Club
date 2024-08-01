package com.adv.ilook.view.ui.fragments.ocrscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adv.ilook.model.data.workflow.OcrScreen
import com.adv.ilook.model.data.workflow.SeeForMeScreen
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
private const val TAG = "==>>OcrScreenViewModel"
@HiltViewModel
class OcrScreenViewModel  @Inject constructor( private val loginRepository: CommonRepository,
                          private val networkHelper: NetworkHelper) :  BaseViewModel(networkHelper) {

    private var ocrScreen by Delegates.notNull<OcrScreen>()

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

    private var nextScreenId_1 by Delegates.notNull<Int>()
    private var nextScreenId_2 by Delegates.notNull<Int>()
    private var previousScreenId by Delegates.notNull<Int>()

    override suspend fun init(function: (TypeOfData) -> Unit) {
        viewModelScope.launch {
            getWorkflowFromJson {
                launch(Dispatchers.Main) {
                    ocrScreen = it.screens?.ocrScreen!!
                    nextScreenId_1 = BasicFunction.getScreens()[getNextScreen(0)] as Int
                    nextScreenId_2 = BasicFunction.getScreens()[getNextScreen(1)] as Int
                    previousScreenId =
                        BasicFunction.getScreens()[ocrScreen.previousScreen] as Int
                    _nextScreenLiveData_1.postValue(nextScreenId_1)
                    _nextScreenLiveData_2.postValue(nextScreenId_2)
                    _prevScreenLiveData.postValue(previousScreenId)
                }

            }
            withContext(Dispatchers.Main) {

            }
        }
    }

    private fun getNextScreen(index:Int) =
        if (ocrScreen.nextScreen == "null") ocrScreen.selectScreen?.get(index)?.nextScreen
        else ocrScreen.nextScreen

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