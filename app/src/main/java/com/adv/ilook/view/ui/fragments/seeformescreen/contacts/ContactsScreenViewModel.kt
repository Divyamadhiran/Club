package com.adv.ilook.view.ui.fragments.seeformescreen.contacts

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.adv.ilook.di.IoDispatcher
import com.adv.ilook.di.MainDispatcher
import com.adv.ilook.model.data.workflow.ContactsScreen
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

private const val TAG = "==>>ContactsScreenViewModel"

@HiltViewModel
class ContactsScreenViewModel @Inject constructor(
    private val loginRepository: SeeForMeRepo,
    private val networkHelper: NetworkHelper,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel(networkHelper) {

    private var contactsScreen by Delegates.notNull<ContactsScreen>()
    private val _nextScreenLiveData = MutableLiveData<Int>()
    var nextScreenLiveData: LiveData<Int> = _nextScreenLiveData
    private val _prevScreenLiveData = MutableLiveData<Int>()
    var prevScreenLiveData: LiveData<Int> = _prevScreenLiveData


    private val _nextScreenLiveData_1 = MutableLiveData<Int>()
    var nextScreenLiveData_1: LiveData<Int> = _nextScreenLiveData_1
    private val _nextScreenLiveData_2 = MutableLiveData<Int>()
    var nextScreenLiveData_2: LiveData<Int> = _nextScreenLiveData_2

    // Define a MutableLiveData property
    private val _allScreenLiveData = MutableLiveData<Map<Int, Any>>()
    val allScreenLiveData: LiveData<Map<Int, Any>> get() = _allScreenLiveData

    init {
        // Initialize the map with an empty map or any initial data
        _allScreenLiveData.value = emptyMap()
    }


    override suspend fun init(function: (TypeOfData) -> Unit) {
        viewModelScope.launch {
            getWorkflowFromJson {
                launch {
                    contactsScreen = it.screens?.contactsScreen!!
                    Log.d(TAG, "init: prev ->${contactsScreen.previousScreen.toString()}")
                    Log.d(TAG, "init: next ->${contactsScreen.nextScreen.toString()}")
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

    private fun getNextScreen(index: Int) =
        if (contactsScreen.nextScreen == "null") contactsScreen.selectScreen?.get(index)?.nextScreen
        else contactsScreen.nextScreen

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

    fun addContactsToFirebase(username: String?, phone: String?) {

       /* if (addNameId != null && phone != null) {
            if (addNameId.toString().isNotEmpty())
        }*/
    }

    private fun isPhoneNumberValid(phone: String): Boolean {
        Log.d(TAG, "isPhoneNumberValid: ${phone.length}")
       /* if (phone.length != 10) {
            return false
        } else {
            addPhoneCountry = "+91$phone"
            Log.d(TAG, "isPhoneNumberValid: ${Patterns.PHONE.matcher(addPhoneCountry).matches()}")
            return Patterns.PHONE.matcher(addPhoneCountry).matches()
        }*/
   return true }

    private fun isUserNameValid(username: String): Boolean {
        return username.trim().length > 3
    }
}