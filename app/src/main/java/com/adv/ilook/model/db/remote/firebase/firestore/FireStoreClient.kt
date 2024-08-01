package com.adv.ilook.model.db.remote.firebase.firestore


import android.util.Log
import com.adv.ilook.di.IoDispatcher
import com.google.firebase.crashlytics.internal.settings.CachedSettingsIo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.memoryCacheSettings
import com.google.firebase.firestore.ktx.persistentCacheSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineDispatcher
import java.lang.reflect.Type
import javax.inject.Inject

private const val TAG_ = "==>>FireStoreClient"
class FireStoreClient @Inject constructor(
    private val fireStore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) :BaseFireStore(fireStore){

    init{
    //    setUpFirebasePersistence(true)
    }
    fun setUpFirebasePersistence(boolean: Boolean){
        fireStore.enableNetwork().addOnCompleteListener {
            if (it.isSuccessful()) {
                Log.d(TAG, "Firestore persistence enabled");
            } else {
                Log.w(TAG, "Firestore persistence enable failed.", it.getException());
            }

        }

        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(boolean) // Enable local cache
            .build()

// Apply custom settings to Firestore instance
        fireStore.firestoreSettings = settings

    }
    suspend fun saveUserData(data:List<Any>){

    }
     fun getUserData(phoneNumber:Any,callback:(Any,Any,Boolean)->Unit){
       //  setUpFirebasePersistence(true)
         val settings = firestoreSettings {
            // setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
             // Use memory cache
             setLocalCacheSettings(memoryCacheSettings {})
             // Use persistent disk cache (default)
             setLocalCacheSettings(persistentCacheSettings {})
         }

         fireStore.firestoreSettings = settings

        val customerDetails = fireStore.collection("customer_details").document(phoneNumber.toString())
        customerDetails.get().addOnSuccessListener {result ->
            val gson = Gson()
            var str = ""

            val gsonType: Type? = object : TypeToken<java.util.HashMap<*, *>?>() {}.type
          
            str = gson.toJson(result.data, gsonType)
            // var genericModel = gson.fromJson(str, GenericModel::class.java)
            Log.d(TAG_, "addOnSuccessListener: Map to jsonStr = $str")

            callback(str, result.id,true)
        }.addOnFailureListener {exception ->
            Log.e(TAG_, "addOnFailureListener: ${getErrorJson(exception)}")
            callback(getErrorJson(exception),0,false)
        }.addOnCompleteListener { message->
            Log.d(TAG_, "addOnCompleteListener:---> $message.result.data")

         //  callback(message.toString(), message.result.id,true)
        }
    }
    fun getErrorJson(exception: Exception): String {
        val errorMap = mapOf("error" to exception.message.toString())
        return Gson().toJson(errorMap)
    }
}