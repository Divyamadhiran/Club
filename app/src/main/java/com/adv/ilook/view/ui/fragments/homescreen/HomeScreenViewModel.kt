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
    private val _menuTitles = MutableLiveData<Map<Int, Pair<String, String>>>()
    val menuTitles: LiveData<Map<Int, Pair<String, String>>> get() = _menuTitles
    private val _homeFieldsEnableFlow = MutableLiveData<Map<Int, Boolean>>()
    val homeFieldsEnableFlow: LiveData<Map<Int, Boolean>> get() = _homeFieldsEnableFlow


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
        val homeFields = mutableMapOf<Int, Boolean>()
        val menuTitles = mutableMapOf<Int, Pair<String, String>>()


        homeScreen.views?.menuView?.forEachIndexed { index, menuViewItem ->
            Log.d(TAG, "Index: $index, Item: $menuViewItem")
            menuViewItem?.let { item ->
                postIfEnabledAndNotEmpty(item.enable, item.text) { text ->
                    val leftIconUrl = item.leftIcon?.url ?: ""
                    when (index) {
                        0 -> menuTitles[R.id.nav_seeforme] = Pair(text, leftIconUrl)
                        1 -> menuTitles[R.id.nav_ocr] = Pair(text, leftIconUrl)
                        2 -> menuTitles[R.id.nav_commodity] = Pair(text, leftIconUrl)
                        3 -> menuTitles[R.id.nav_ai] = Pair(text, leftIconUrl)
                        4 -> menuTitles[R.id.nav_logout] = Pair(text, leftIconUrl)
                        5 -> menuTitles[R.id.nav_about] = Pair(text, leftIconUrl)
                    }


                    // Always update menuTitles with latest value
                    // Call updateMap with latest index and item
                    updateMap(index, item)
                }


                // Update homeFields based on index and enable status
                val isEnable = item.enable ?: false
                when (index) {
                    0 -> homeFields[R.id.nav_seeforme] = isEnable
                    1 -> homeFields[R.id.nav_ocr] = isEnable
                    2 -> homeFields[R.id.nav_commodity] = isEnable
                    3 -> homeFields[R.id.nav_ai] = isEnable
                    4 -> homeFields[R.id.nav_logout] = isEnable
                    5 -> homeFields[R.id.nav_about] = isEnable
                }
            } ?: run {
                Log.d(TAG, "Item at index $index is null") // Debugging log for null items
            }
        }


        // Post value to _homeFieldsEnableFlow after the loop
        _menuTitles.postValue(menuTitles)
        _homeFieldsEnableFlow.postValue(homeFields)
        Log.d(TAG, "updateViewElements: menuTitles $menuTitles")
        Log.d(TAG, "updateViewElements: homeFields $homeFields")
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
