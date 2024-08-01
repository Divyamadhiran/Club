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
import com.adv.ilook.model.util.responsehelper.UiStatusLogin
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

    // Define a MutableLiveData property
    private val _allScreenLiveData = MutableLiveData<Map<Int, Any>>()
    val allScreenLiveData: LiveData<Map<Int, Any>> get() = _allScreenLiveData

    private lateinit var addUserName: String
    private lateinit var addPhoneNumber: String

    init {
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

        if (username != null && phone != null) {
            addUserName = username
            addPhoneNumber = phone
            if (isPhoneNumberValid(phone) && isUserNameValid(username)) {
                loginRepository.addContactsToFirebase(addUserName, addPhoneNumber)
            } else {

            }
        }
    }

    fun userNameState(username: String) {
        if (!isUserNameValid(username)) {
            _loginFormState.postValue(
                UiStatusLogin.LoginFormState(
                    isNameError = true,
                    usernameError = "Username must be greater than 3"
                )
            )
        } else {
            _loginFormState.postValue(UiStatusLogin.LoginFormState(isNameError = false))
        }
    }

    fun userPhoneState(phone: String) {
        if (!isPhoneNumberValid(phone)) {
            Log.d(TAG, "addContactsStateChange: ElseIf-${phone.length}")
            _loginFormState.postValue(
                UiStatusLogin.LoginFormState(
                    isPhoneError = true,
                    phoneError = "Phone number must be 10 digits"
                )
            )
        } else {
            _loginFormState.postValue(UiStatusLogin.LoginFormState(isPhoneError = false))
        }
    }

    fun addContactsStateChange(username: String, phone: String) {
        if (isUserNameValid(username) && isPhoneNumberValid(phone)) {
            Log.d(
                TAG,
                "addContactsStateChange:valid both phone=${phone.length} name=${username.length}"
            )
            _loginFormState.postValue(UiStatusLogin.LoginFormState(message = "Valid username and phone number"))
            _loginFormState.postValue(UiStatusLogin.LoginFormState(isDataValid = true))
        } else {
            if (!isUserNameValid(username))
                _loginFormState.postValue(
                    UiStatusLogin.LoginFormState(
                        isNameError = true,
                        usernameError = "Username must be greater than 3"
                    )
                )
            else if (!isPhoneNumberValid(phone))
                _loginFormState.postValue(
                    UiStatusLogin.LoginFormState(
                        isPhoneError = true,
                        phoneError = "Phone number must be 10 digits"
                    )
                )
        }
    }

    private fun isPhoneNumberValid(phone: String): Boolean {
        Log.d(TAG, "isPhoneNumberValid: ${phone.length}")
        if (phone.length != 10) {
            return false
        } else {
            addPhoneNumber = "+91$phone"
            Log.d(TAG, "isPhoneNumberValid: ${Patterns.PHONE.matcher(addPhoneNumber).matches()}")
            return Patterns.PHONE.matcher(addPhoneNumber).matches()
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return username.trim().length > 3
    }

    fun countryCodeAdd(phone: String): String {
        if (phone.length > 10) {
            Log.d(TAG, "countryCodeAdd: data=+91${phone.reversed().substring(0, 10).reversed()}")
            return phone.reversed().substring(0, 10).reversed()
        }
        //  addPhoneCountry = "+91$phone"
        return phone
    }

    private val _loginFormState = MutableLiveData<UiStatusLogin>()
    val loginFormState: LiveData<UiStatusLogin> = _loginFormState
}