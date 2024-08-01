package com.adv.ilook.model.db.remote.firebase.realtimedatabase


import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.adv.ilook.model.data.firebasemodel.FireStoreResponse
import com.adv.ilook.model.db.remote.firebase.firestore.FireStoreClient
import com.adv.ilook.model.util.assets.FirebaseKeys
import com.adv.ilook.model.util.assets.FirebaseKeys.user_name
import com.adv.ilook.model.util.assets.UserStatus
import com.adv.ilook.view.ui.fragments.dataclasses.ContactList
import com.adv.ilook.view.ui.fragments.dataclasses.AllContactsInfo
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
        token: String,
        done: (Boolean, Any?) -> Unit
    ): Boolean {

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
//
//    suspend fun observeContactDetails(
//        status: (List<ContactList>) -> Unit,
//        status3: (List<ContactList>) -> Unit,
//    ) {
//        val finalList = mutableListOf<AllContactsInfo>()
//
//        val outerList = mutableListOf<AllContactsInfo>()
//        val withAcc = mutableListOf<AllContactsInfo>()
//        //val withAcc1 = MutableLiveData<List<AllContactsInfo>(mutableListOf<AllContactsInfo>())
//        val innerList = mutableListOf<AllContactsInfo>()
//        val unregisteredContacts = mutableListOf<AllContactsInfo>()
//
//        databaseReference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                withAcc.clear()
//                outerList.clear()
//                innerList.clear()
//                unregisteredContacts.clear()
//                // Process all users except the current user
//                snapshot.children.filter { it.key != rigisteredPhoneNumber }
//                    .forEach { userSnapshot ->
//                        val userInfo = AllContactsInfo(
//                            userSnapshot.key.toString(),
//                            userSnapshot.child(user_name).value.toString(),
//                            userSnapshot.child(FirebaseKeys.status).value.toString(),
//                            isCforMeAcc = true
//                        )
//                        outerList.add(userInfo)
//                    }
//
//                // Process current user's contacts
//                snapshot.children.filter { it.key == rigisteredPhoneNumber }
//                    .forEach { currentUserSnapshot ->
//                        currentUserSnapshot.child("contacts")
//                            .children.filter { it.key != rigisteredPhoneNumber }
//                            .forEach { contactSnapshot ->
//                                val contactInfo = AllContactsInfo(
//                                    contactSnapshot.key.toString(),
//                                    contactSnapshot.child(user_name).value.toString(),
//                                    contactSnapshot.child(FirebaseKeys.status).value.toString(),
//                                    isCforMeAcc = false
//                                )
//                                innerList.add(contactInfo)
//                            }
//                    }
//
//                Log.d("***TAG", "All contacts: $outerList")
//                Log.d("***TAG", "Current user contacts: $innerList")
//
//                // Use a map for quick lookup of registered contacts
//                val outerContactMap = outerList.associateBy { it.contactNumber.trim() }
//                Log.d(TAG, "onDataChange: outerContactMap =$outerContactMap")
//
//                innerList.forEach { innerContact ->
//                    val matchedOuterContact = outerContactMap[innerContact.contactNumber.trim()]
//                    matchedOuterContact?.let { outerContact ->
//                        innerContact.isCforMeAcc = true
//                        innerContact.status = outerContact.status
//                    } ?: run {
//                        innerContact.status = UserStatus.OFFLINE.name
//                        unregisteredContacts.add(innerContact)
//                    }
//                    withAcc.add(innerContact)
//                }
//
//                withAcc1.value = withAcc.toSet().toList()
//                if (withAcc1.value != null)
//                    status3(withAcc1.value!!)
//                else
//                    status3(withAcc.toSet().toList())
//                Log.d("***TAG", "Updated contacts with status: $withAcc")
//                Log.d("***TAG", "Unregistered contacts: $unregisteredContacts")
//
//                // Optionally, you can call a different status function for unregistered contacts
//                status(unregisteredContacts)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.e("***TAG", "Error: $error")
//            }
//        })
//
//        withAcc1.observe(ctx) { data ->
//            data.forEach { innerContact ->
//                val statusUpdateTask = databaseReference.child(rigisteredPhoneNumber!!)
//                    .child(FirebaseKeys.contacts)
//                    .child(innerContact.contactNumber)
//                    .child(FirebaseKeys.status)
//                    .setValue(innerContact.status)
//                statusUpdateTask.addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        Log.d(
//                            "***TAG",
//                            "Successfully updated status for contact: ${innerContact.contactNumber}"
//                        )
//                    } else {
//                        Log.e(
//                            "***TAG",
//                            "Failed to update status for contact: ${innerContact.contactNumber}",
//                            task.exception
//                        )
//                    }
//                }
//                statusUpdateTask.addOnFailureListener { exception ->
//                    Log.e(
//                        "***TAG",
//                        "Failed to update status for contact: ${innerContact.contactNumber}",
//                        exception
//                    )
//                }
//            }
//        }
//    }
//
//


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

    fun addContactsToFirebase(addUserName: String, addPhoneNumber: String) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(rigisteredPhoneNumber)) {
                    databaseReference.child(rigisteredPhoneNumber).child(FirebaseKeys.contacts)
                        .child(addPhoneNumber)
                        .child(user_name).setValue(addUserName).addOnCompleteListener {
                            databaseReference.child(rigisteredPhoneNumber).child(FirebaseKeys.contacts)
                                .child(addPhoneNumber).child(FirebaseKeys.status)
                            .setValue(UserStatus.OFFLINE.name).addOnCompleteListener {
                                Log.d(TAG, "addContactsToFirebase: added successfully")
                            }
                                .addOnFailureListener {
                                    Log.d(TAG, "addContactsToFirebase: ${it.message}")
                                }
                        }.addOnFailureListener {
                            Log.d(TAG, "addContactsToFirebase: ${it.message}")
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: ${error.message}")
            }

        })
    }


}