package com.adv.ilook.view.ui.fragments.homescreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.adv.ilook.R
import com.adv.ilook.di.IoDispatcher
import com.adv.ilook.di.MainDispatcher
import com.adv.ilook.model.data.workflow.HomeScreen
import com.adv.ilook.model.db.remote.repository.apprepo.SeeForMeRepo
import com.adv.ilook.model.util.network.NetworkHelper
import com.adv.ilook.view.base.BaseViewModel
import com.adv.ilook.view.base.BasicFunction
import com.adv.ilook.view.ui.fragments.splash.TypeOfData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import kotlin.properties.Delegates

private const val TAG = "==>>HomeScrViewModel"

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val loginRepository: SeeForMeRepo,
    networkHelper: NetworkHelper,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,

) : BaseViewModel(networkHelper) {

    private var homeScreen by Delegates.notNull<HomeScreen>()
    private val _nextScreenLiveData = MutableLiveData<Int>()
    var nextScreenLiveData: LiveData<Int> = _nextScreenLiveData
    private val _prevScreenLiveData = MutableLiveData<Int>()
    var prevScreenLiveData: LiveData<Int> = _prevScreenLiveData
    private val _menuTitles = MutableLiveData<Map<Int, Pair<String,String>>>()
    val menuTitles: LiveData<Map<Int, Pair<String,String>>> get() = _menuTitles


    // Define a MutableLiveData property
    private val _allScreenLiveData = MutableLiveData<Map<Int, Any>>()

    // Expose the LiveData to observers
    val allScreenLiveData: LiveData<Map<Int, Any>> get() = _allScreenLiveData
    override suspend fun init(function: (TypeOfData) -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            getWorkflowFromJson {
                launch(Dispatchers.Main) {
                    homeScreen = it.screens?.homeScreen!!
                    Log.d(TAG, "init: prev ->${homeScreen.previousScreen}")
                    Log.d(TAG, "init: next ->${homeScreen.nextScreen}")
                    //_nextScreenLiveData.postValue(BasicFunction.getScreens()[homeScreen.selectScreen?.get(3)?.nextScreen] as Int)
                    //_prevScreenLiveData.postValue(BasicFunction.getScreens()[homeScreen.selectScreen?.get(3)?.previousScreen] as Int)
                    function(TypeOfData.INT)
                    // Update menu titles from homeScreen
                    launch {
                        updateViewElements(homeScreen)

                    }
                }

            }

        }
    }

    private fun updateViewElements(homeScreen: HomeScreen) {
        homeScreen.apply {
            views?.menuView?.forEachIndexed { index, menuViewItem ->
                postIfEnabledAndNotEmpty(menuViewItem?.enable,menuViewItem?.text){
                    _menuTitles.value =buildMap<Int,Pair<String,String>> {
                        Log.d(TAG, "updateViewElements: index $index value=$it")
                       when(index){
                           0 ->{

                               put(R.id.nav_seeforme, Pair(it,menuViewItem?.leftIcon?.url!!))

                           }
                           1 ->{
                               put(R.id.nav_ocr, Pair(it,menuViewItem?.leftIcon?.url!!))
                           }
                           2 ->{
                               put(R.id.nav_commodity, Pair(it,menuViewItem?.leftIcon?.url!!))
                           }
                           3 ->{
                               put(R.id.nav_ai, Pair(it,menuViewItem?.leftIcon?.url!!))
                           }
                           4 ->{
                               put(R.id.nav_logout, Pair(it,menuViewItem?.leftIcon?.url!!))
                           }
                           5 ->{
                               put(R.id.nav_about, Pair(it,menuViewItem?.leftIcon?.url!!))
                           }

                       }
                   }
                    updateMap(index, menuViewItem!!)
                }
            }
        }
    }

    private fun getNextScreen(index: Int) =
        if (homeScreen.nextScreen == "null") homeScreen.selectScreen?.get(index)?.nextScreen
        else homeScreen.nextScreen

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


    fun handleNavigationItemSelected(itemId: Int) {
        when (itemId) {
            R.id.nav_seeforme -> {
                Log.d(TAG, "handleNavigationItemSelected: nav_seeforme")
            }

            R.id.nav_ocr -> {
                Log.d(TAG, "handleNavigationItemSelected: nav_ocr")
            }

            R.id.nav_commodity -> {
                Log.d(TAG, "handleNavigationItemSelected: nav_commodity")
            }

            R.id.nav_ai -> {
                Log.d(TAG, "handleNavigationItemSelected: nav_ai")
            }

            R.id.nav_logout -> {
                Log.d(TAG, "handleNavigationItemSelected: nav_logout")
            }

            R.id.nav_about -> {
                Log.d(TAG, "handleNavigationItemSelected: nav_about")
            }
        }
    }

    //Menu Title
    val _nav_seeforme_title = MutableLiveData<String>()
    val nav_seeforme_title: LiveData<String> get() = _nav_seeforme_title


    val _nav_ocr_title = MutableLiveData<String>()
    val nav_ocr_title: LiveData<String> get() = _nav_ocr_title


    val _nav_commodity_title = MutableLiveData<String>()
    val nav_commodity_title: LiveData<String> get() = _nav_commodity_title


    val _nav_ai_title = MutableLiveData<String>()
    val nav_ai_title: LiveData<String> get() = _nav_ai_title


    val _nav_logout_title = MutableLiveData<String>()
    val nav_logout_title: LiveData<String> get() = _nav_logout_title


    val _nav_about_title = MutableLiveData<String>()
    val nav_about_title: LiveData<String> get() = _nav_about_title


//Menu icon

    val _nav_seeforme_icon = MutableLiveData<String>()
    val nav_seeforme_icon: LiveData<String> get() = _nav_seeforme_icon


    val _nav_ocr_icon = MutableLiveData<String>()
    val nav_ocr_icon: LiveData<String> get() = _nav_ocr_title


    val _nav_commodity_icon = MutableLiveData<String>()
    val nav_commodity_icon: LiveData<String> get() = _nav_commodity_icon


    val _nav_ai_icon = MutableLiveData<String>()
    val nav_ai_icon: LiveData<String> get() = _nav_ai_icon


    val _nav_logout_icon = MutableLiveData<String>()
    val nav_logout_icon: LiveData<String> get() = _nav_logout_icon


    val _nav_about_icon = MutableLiveData<String>()
    val nav_about_icon: LiveData<String> get() = _nav_about_icon
}
