package com.adv.ilook.model.db.remote.firebase.realtimedatabase


import android.app.Activity
import android.util.Log
import com.adv.ilook.model.data.firebasemodel.FireStoreResponse
import com.adv.ilook.model.db.remote.firebase.firestore.FireStoreClient
import com.adv.ilook.model.util.assets.FirebaseKeys

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.properties.Delegates
private const val TAG = "==>>FirebaseClient"
class FirebaseClient @Inject constructor(
    private val databaseReference: DatabaseReference,
    private val auth: FirebaseAuth,
    private val fireStoreClient: FireStoreClient,
    private val ioDispatcher: CoroutineDispatcher
) : BaseRealTimeDataBase(databaseReference) {
    private var verificationId: String? = null
    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null
    private var rigisteredPhoneNumber by Delegates.notNull<String>()
    private var rigisteredUserName by Delegates.notNull<String>()

    suspend fun login(
        username: String,
        phoneNumber: String,
        status: String, //ONLINE,OFFLINE,UNREGISTER,IN_CALL
        isLogged: Boolean,
        token:String,
        done: (Boolean, Any?) -> Unit
    ):Boolean {

       return suspendCoroutine { continuation ->

            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(phoneNumber)) {
                        val dbPhoneNumber = snapshot.child(phoneNumber).key.toString()
                        if (phoneNumber == dbPhoneNumber) {
                            val dbChild = databaseReference.child(phoneNumber)
                            dbChild.child(FirebaseKeys.user_name).setValue(username)
                            dbChild.child(FirebaseKeys.status).setValue(status)
                            dbChild.child(FirebaseKeys.login).setValue(isLogged)
                            dbChild.child(FirebaseKeys.token).setValue(token)
                                .addOnSuccessListener {
                                    rigisteredPhoneNumber = phoneNumber
                                    rigisteredUserName = username
                                    Log.d(TAG, "onDataChange: saved successfully")
                                    done(true, "saved successfully")
                                    continuation.resume(true)
                                }
                               .addOnFailureListener {
                                    done(false, it.message)
                                    continuation.resumeWithException(it)
                                }
                        } else {
                            done(false, "phone number is wrong")
                            continuation.resumeWithException(Exception("Phone number is wrong"))
                        }
                    } else {
                        CoroutineScope(Dispatchers.IO).launch {
                            fireStoreClient.getUserData(phoneNumber) { jsonStr: Any, docId: Any, status: Boolean ->
                                if (jsonStr.toString().isNotEmpty()) {
                                    val fireStoreResponse = Gson().fromJson(
                                        jsonStr.toString(),
                                        FireStoreResponse::class.java
                                    )
                                    Log.d(TAG, "onDataChange: , ${jsonStr} ,status:->$status")
                                    if (status) {
                                        if (phoneNumber == fireStoreResponse.phoneNumber) {
                                            val dbChild = databaseReference.child(phoneNumber)
                                            dbChild.child(FirebaseKeys.user_name).setValue(username)
                                            dbChild.child(FirebaseKeys.status).setValue(status)
                                            dbChild.child(FirebaseKeys.login).setValue(isLogged)
                                            dbChild.child(FirebaseKeys.token).setValue(token)
                                                .addOnSuccessListener {
                                                    rigisteredPhoneNumber = phoneNumber
                                                    rigisteredUserName = username
                                                    Log.d(TAG, "onDataChange: saved successfully")
                                                    done(true, "saved successfully")
                                                    continuation.resume(true)
                                                }
                                               .addOnFailureListener {
                                                    Log.d(TAG, "onDataChange: addOnFailureListener")
                                                    done(false, it.message)
                                                    continuation.resumeWithException(Exception(it.message))
                                                }

                                        } else {
                                            Log.d(TAG, "onDataChange: Device is not purchased")
                                            done(false, "Device is not purchased")
                                            continuation.resumeWithException(Exception("Device is not purchased"))
                                        }

                                    } else {
                                        Log.e(TAG, "onDataChange: ${fireStoreResponse.errorMsg}")
                                        done(false, "Something went wrong")
                                        continuation.resumeWithException(Exception("Something went wrong"))
                                    }
                                }
                            }
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    done(false, error.message)
                    continuation.resumeWithException(Exception(error.message))
                }
            })

        }
    }


    fun logout(
        username: String, phone: String, status: String,
        isLogged: Boolean, callback: (Any?) -> Unit
    ) {

    }

    fun sendVerificationCode(
        activity: Activity,
        phone: String,
        phoneAuthCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(phoneAuthCallback)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyCode(code: String): PhoneAuthCredential {
        return PhoneAuthProvider.getCredential(verificationId!!, code)
    }

    fun signInWithCredential(
        credential: PhoneAuthCredential,
        callback: (FirebaseUser?, Exception?) -> Unit
    ) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(task.result?.user, null)
                } else {
                    callback(null, task.exception)
                }
            }
    }


    fun setVerificationIdAndToken(
        verificationId: String,
        token: PhoneAuthProvider.ForceResendingToken
    ) {
        this.verificationId = verificationId
        this.forceResendingToken = token
    }


}