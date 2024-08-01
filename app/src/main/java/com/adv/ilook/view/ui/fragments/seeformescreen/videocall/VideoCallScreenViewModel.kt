package com.adv.ilook.view.ui.fragments.seeformescreen.videocall

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adv.ilook.di.IoDispatcher
import com.adv.ilook.di.MainDispatcher
import com.adv.ilook.model.data.workflow.HomeScreen
import com.adv.ilook.model.db.remote.repository.apprepo.SeeForMeRepo
import com.adv.ilook.model.util.network.NetworkHelper
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import kotlin.properties.Delegates

private const val TAG = "==>>VideoCallScreenViewModel"
class VideoCallScreenViewModel@Inject constructor(
    private val loginRepository: SeeForMeRepo,
    private val networkHelper: NetworkHelper,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel()
{

    private var videoCallScreenViewModel by Delegates.notNull<HomeScreen>()
    private val _nextScreenLiveData = MutableLiveData<Int>()
    var nextScreenLiveData: LiveData<Int> = _nextScreenLiveData
    private val _prevScreenLiveData = MutableLiveData<Int>()
    var prevScreenLiveData: LiveData<Int> = _prevScreenLiveData

}