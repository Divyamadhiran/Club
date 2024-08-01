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
    val _splashFieldsEnable = MutableLiveData<Map<Int, Boolean>>()
    val splashFieldsEnable: LiveData<Map<Int, Boolean>> get() = _splashFieldsEnable


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
        val fields = mutableMapOf<Int, Boolean>()
        var imageViewUrl = ""
        var headTextViews = ""


        splashWorkflow.views?.imageView?.icon.let { icon ->
            postIfEnabledAndNotEmpty(icon?.enable ?: false, icon?.url ?: "") {
                Log.d(TAG, "updateSplashScreenFromJson: ${icon?.enable}")
                imageViewUrl = it
            }
        }


        splashWorkflow.views?.textView?.header?.let { header ->
            postIfEnabledAndNotEmpty(header.enable ?: false, header.text ?: "") {
                headTextViews = it
            }
        }


        _splashFields.postValue(
            mapOf(
                R.id.back_IView to imageViewUrl,
                R.id.head_text_TV to headTextViews
            )
        )
        fields[R.id.back_IView] = splashWorkflow.views?.imageView?.icon?.enable ?: false
        fields[R.id.head_text_TV] = splashWorkflow.views?.textView?.header?.enable ?: false
        fields[R.id.child_text_TV] = splashWorkflow.views?.textView?.bodyText?.enable ?: false
        Log.d(TAG, "updateSplashScreenFromJson: $fields")
        _splashFieldsEnable.postValue(fields)


    }


}