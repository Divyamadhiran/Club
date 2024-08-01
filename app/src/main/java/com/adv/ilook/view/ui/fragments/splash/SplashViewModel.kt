package com.adv.ilook.view.ui.fragments.splash

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adv.ilook.R
import com.adv.ilook.model.data.workflow.SplashScreen
import com.adv.ilook.model.util.network.NetworkHelper
import com.adv.ilook.view.base.BaseViewModel
import com.adv.ilook.view.base.BasicFunction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.properties.Delegates


enum class TypeOfData {
    INT, STRING, ANY, ELSE
}

private const val TAG = "==>>SplashViewModel"

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val networkHelper: NetworkHelper
) : BaseViewModel(networkHelper) {
    private val _nextScreenLiveData = MutableLiveData<Int>()
    var nextScreenLiveData: LiveData<Int> = _nextScreenLiveData
    val _splashFields = MutableLiveData<Map<Int, String>>()
    val splashFields: LiveData<Map<Int, String>> get() = _splashFields
    private var splashWorkflow by Delegates.notNull<SplashScreen>()
    override fun callOtherActivity(activity: Activity, msg: String) {

    }

    override suspend fun init(function: (TypeOfData) -> Unit) {
        viewModelScope.launch {
            getWorkflowFromJson {
                launch(Dispatchers.Main) {
                    splashWorkflow=it.screens?.splashScreen!!
                    Log.d(TAG, "init: ${it.screens.splashScreen.nextScreen}")
                    val nextPage = BasicFunction.getScreens()[it.screens.splashScreen.nextScreen]
                    when (nextPage) {
                        is Int -> {
                            _nextScreenLiveData.postValue(nextPage as Int)
                            function(TypeOfData.INT)
                        }
                        else -> {
                            function(TypeOfData.ANY)
                        }
                    }
                }
                launch(Dispatchers.Main) {
                    updateSplashScreenFromJson(splashWorkflow)
                }
            }
        }

    }

    fun updateSplashScreenFromJson(splashWorkflow: SplashScreen) {
        var imageView = ""
        var headtextViews = ""
        if (splashWorkflow.views?.imageView?.icon?.enable!!) {
            if (splashWorkflow.views.imageView.icon.url.toString().isNotEmpty()) {
                imageView = splashWorkflow.views.imageView.icon.url.toString()
            }
        }
        if (splashWorkflow.views.textView?.header?.enable!!) {
            if (splashWorkflow.views.textView.header.text.toString().isNotEmpty()) {
                headtextViews = splashWorkflow.views.textView!!.header?.text.toString()
            }
        }


        _splashFields.postValue(
            mapOf(
                R.id.back_IMV to imageView,
                R.id.header_SplashText_TV to headtextViews
            )
        )
    }

}