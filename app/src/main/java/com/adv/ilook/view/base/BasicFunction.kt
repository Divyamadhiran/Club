package com.adv.ilook.view.base

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.work.ListenableWorker
import com.adv.ilook.R
import com.adv.ilook.model.db.remote.firebase.remoteconfig.RemoteConfig
import com.adv.ilook.model.util.extension.launchOnIO
import com.adv.ilook.model.util.extension.launchOnMain
import com.adv.ilook.model.util.extension.scope
import com.adv.ilook.model.util.network.NetworkHelper
import com.adv.ilook.view.ui.fragments.homescreen.HomeScreenFragment
import com.adv.ilook.view.ui.fragments.instruction.InstructionFragment
import com.adv.ilook.view.ui.fragments.loginscreen.LoginFragment
import com.adv.ilook.view.ui.fragments.ocrscreen.OcrScreenFragment
import com.adv.ilook.view.ui.fragments.otpscreen.OtpFragment
import com.adv.ilook.view.ui.fragments.seeformescreen.SeeformeScreenFragment
import com.adv.ilook.view.ui.fragments.seeformescreen.callhistory.CallhistoryScreenFragment
import com.adv.ilook.view.ui.fragments.seeformescreen.calogs.CallLogsScreenFragment
import com.adv.ilook.view.ui.fragments.seeformescreen.contacts.ContactsScreenFragment
import com.adv.ilook.view.ui.fragments.seeformescreen.videocall.VideoCallScreenFragment
import com.adv.ilook.view.ui.fragments.selectscreen.SelectScreenFragment
import com.adv.ilook.view.ui.fragments.splash.SplashFragment
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.time.Duration
import java.time.LocalDateTime
import java.util.Base64
import javax.crypto.Cipher
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class BasicFunction @Inject constructor(
    context: Context?,
    private val remoteConfig: RemoteConfig,
    private val networkHelper: NetworkHelper
) {

    private val TAG = "==>>BasicFunction"
    private val scope = CoroutineScope(Job() + Dispatchers.Main)
    fun getFileFromRemoteConfig(
        context: Context?,
        callFromActivity: Boolean,
        callback: (Boolean, String) -> Unit,
        notifyCallback: (Boolean, String) -> Unit
    ) {
        Log.d(TAG, "getFileFromRemoteConfig: networkHelper =${networkHelper.isNetworkConnected()}")
        scope.launchOnIO {
            withContext(Dispatchers.IO) {
                networkHelper.isNetworkConnected {
                    if (callFromActivity) {
                        val isAvailableNetwork = it as Boolean
                        if (isAvailableNetwork) {
                            Log.d(TAG, "getFileFromRemoteConfig: network available")
                            /* remoteConfig.fetchAndActivate(
                                 context,
                                 callback
                             )*/
                         //  launch(Dispatchers.IO) {
                                remoteConfig.remoteUpdateConfig(
                                    context,
                                    notifyCallback
                                )
                         //  }

                        } else {
                            Log.d(TAG, "getFileFromRemoteConfig: network not available")
                           // launch(Dispatchers.IO) {
                                remoteConfig.fetchAndActivate(
                                    context,
                                    callback
                                )
                          // }
                        }
                    }
                }
            }

        }


    }

    fun getFileFromAsset(fileName: String, context: Context): String {
        val builderContents = StringBuilder()
        val bufferReader: BufferedReader
        try {
            bufferReader = BufferedReader(InputStreamReader(context.assets.open(fileName)))
            var mLine: String?
            while (bufferReader.readLine().also { mLine = it } != null) {
                builderContents.append(mLine)
            }
            bufferReader.close()
        } catch (e: IOException) {
            Log.e(TAG, "getFileFromAsset error: ${e.message}")
        }
        return builderContents.toString()
    }

    fun saveJsonToFile(context: Context, jsonString: String, fileName: String): String {

        val file = File(context.filesDir, fileName)
        file.writeText(jsonString)

        return file.absolutePath
    }

    fun readJsonFromFile(filePath: String): String {
        return File(filePath).readText()
    }

    fun removeWhiteSpace(strings: String, function: (String) -> Unit): String {
        val pattern = "\\s+".toRegex()
        val res = strings.replace(pattern, "")
        function(res)
        return res
    }

    object Fun {
        fun spiltContainsComma(
            delimeter: Char,
            strings: String,
            function: (List<String>) -> Unit
        ): List<String> {

            val res = strings.split(delimeter).map { it.trim() }
            function(res)
            return res
        }
    }

    companion object Screens {
        private var scr = hashMapOf<String, Any>()
        fun getScreens(): HashMap<String, Any> {
            //splashScreen
            scr["splash_to_splash"] = R.id.action_splashFragment_self
            scr["splash_screen_to_login_screen"] = R.id.action_splashFragment_to_loginFragment
            scr["splash_screen_to_select_screen_type"] =
                R.id.action_splashFragment_to_selectScreenFragment

            //loginScreen
            scr["login_to_login"] = R.id.action_loginFragment_self
            scr["login_screen_to_home_screen"] = R.id.action_splashFragment_to_loginFragment
            scr["login_screen_to_instruction_screen"] =
                R.id.action_loginFragment_to_instructionFragment
            scr["login_screen_to_otp_screen"] = R.id.action_loginFragment_to_otpFragment


            //otpScreen
            scr["otp_screen_to_login_screen"] = R.id.action_otpFragment_to_loginFragment
            scr["otp_screen_to_home_screen"] = R.id.action_otpFragment_to_homeScreenFragment


            //selectScreen
            scr["select_screen_type_to_select_screen_type"] = R.id.action_selectScreenFragment_self
            scr["select_screen_type_to_instruction_screen"] =
                R.id.action_selectScreenFragment_to_instructionFragment

            scr["select_screen_type_to_login_screen"] =
                R.id.action_selectScreenFragment_to_loginFragment


            //instructionScreen

            scr["instruction_screen_to_login_screen"] =
                R.id.action_instructionFragment_to_loginFragment
            scr["instruction_screen_to_select_screen_type"] =
                R.id.action_instructionFragment_to_selectScreenFragment


            //homeScreen
            scr["home_screen_to_home_screen"] = R.id.action_homeScreenFragment_self
            scr["home_screen_to_see_for_me_screen"] =
                R.id.action_homeScreenFragment_to_seeformeScreenFragment
            scr["home_screen_to_ocr_screen"] =
                R.id.action_homeScreenFragment_to_ocrScreenFragment
            scr["home_screen_to_ai_navigator_screen"] =
                R.id.action_homeScreenFragment_to_aiNavigatorFragment


            //seeformeScreen
            scr["see_for_me_screen_to_home_screen"] =
                R.id.action_seeformeScreenFragment_to_homeScreenFragment
            scr["see_for_me_screen_to_contacts_screen"] =
                R.id.action_seeformeScreenFragment_to_contactsScreenFragment
            scr["see_for_me_screen_to_call_logs_screen"] =
                R.id.action_seeformeScreenFragment_to_callLogsScreenFragment
            scr["see_for_me_screen_to_call_history_screen"] =
                R.id.action_seeformeScreenFragment_to_callhistoryScreenFragment

            //ContactsScreen
            scr["contacts_screen_to_see_for_me_screen"] =
                R.id.action_contactsScreenFragment_to_seeformeScreenFragment
            scr["contacts_screen_to_video_call_screen"] =
                R.id.action_contactsScreenFragment_to_videoCallScreenFragment


            //CallLogsScreen
            scr["call_logs_screen_to_see_for_me_screen"] =
                R.id.action_callLogsScreenFragment_to_seeformeScreenFragment
            scr["call_logs_screen_to_video_call_screen"] =
                R.id.action_callLogsScreenFragment_to_videoCallScreenFragment


            //CallHistoryScreen
            scr["call_history_screen_to_see_for_me_screen"] =
                R.id.action_callhistoryScreenFragment_to_seeformeScreenFragment
            scr["call_history_screen_to_video_call_screen"] =
                R.id.action_callhistoryScreenFragment_to_videoCallScreenFragment

//video_call_screen
            scr["video_call_screen_to_see_for_me_screen"] =
                R.id.action_videoCallScreenFragment_to_seeformeScreenFragment

            //backward
            scr["null"] = 0
            scr["finish"] = -1


            return scr
        }

        fun getFragments(key: String): Fragment =
            when (key) {

                "fragment_splash_screen" -> SplashFragment.newInstance()
                "fragment_select_screen" -> SelectScreenFragment.newInstance()
                "fragment_instruction_screen" -> InstructionFragment.newInstance()
                "fragment_login_screen" -> LoginFragment.newInstance()
                "fragment_otp_screen" -> OtpFragment.newInstance()
                "fragment_home_screen" -> HomeScreenFragment.newInstance()
                "fragment_see_for_me_screen" -> SeeformeScreenFragment.newInstance()
                "fragment_ocr_screen" -> OcrScreenFragment.newInstance()

                "see_for_me_screen_to_call_history_screen",
                "fragment_call_history_screen" -> CallhistoryScreenFragment.newInstance()

                "see_for_me_screen_to_call_logs_screen",
                "fragment_call_logs_screen" -> CallLogsScreenFragment.newInstance()

                "see_for_me_screen_to_contacts_screen",
                "fragment_contacts_screen" -> ContactsScreenFragment.newInstance()

                "video_call_screen_to_see_for_me_screen",
                "fragment_video_call_screen" -> VideoCallScreenFragment.newInstance()

                else -> SplashFragment.newInstance()
            }

    }

    object EndToEnd {
        fun generateKeyPair(): KeyPair {
            val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
            keyPairGenerator.initialize(2048, SecureRandom())
            return keyPairGenerator.generateKeyPair()
        }

        fun encrypt(message: String, publicKey: PublicKey): String {
            val cipher = Cipher.getInstance("RSA")
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            val encryptedBytes = cipher.doFinal(message.toByteArray())
            return Base64.getEncoder().encodeToString(encryptedBytes)
        }

        fun decrypt(encryptedMessage: String, privateKey: PrivateKey): String {
            val cipher = Cipher.getInstance("RSA")
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            val decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage))
            return String(decryptedBytes)
        }
    }
}