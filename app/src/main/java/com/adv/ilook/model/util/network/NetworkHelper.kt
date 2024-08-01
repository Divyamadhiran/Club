package com.adv.ilook.model.util.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.google.android.datatransport.runtime.dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "==>>NetworkHelper"
@Singleton
class NetworkHelper @Inject constructor(@ApplicationContext private val context: Context) {
    @OptIn(DelicateCoroutinesApi::class)
    fun isNetworkConnected(callback: (Any) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {

            try {
                val process = Runtime.getRuntime().exec("/system/bin/ping -c 1 8.8.8.8")
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                val output = StringBuffer()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    output.append(line)
                }

                reader.close()
                process.waitFor()
                // process.exitValue() == 0
                callback(process.exitValue() == 0)
            } catch (e: Exception) {
                e.printStackTrace()
                //false
                Log.d(TAG, "isNetworkConnected: error: ${e.message}")
                callback(false)
            }

        }

    }
    fun isNetworkConnected(): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }
}