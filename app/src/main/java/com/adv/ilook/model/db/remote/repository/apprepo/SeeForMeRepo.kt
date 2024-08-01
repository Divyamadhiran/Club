package com.adv.ilook.model.db.remote.repository.apprepo

import android.app.Activity
import com.adv.ilook.di.IoDispatcher
import com.adv.ilook.model.data.workflow.Workflow
import com.adv.ilook.model.db.local.source.CommonDataSource
import com.adv.ilook.model.db.remote.firebase.firestore.FireStoreClient
import com.adv.ilook.model.db.remote.firebase.realtimedatabase.FirebaseClient
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Response
import javax.inject.Inject


class SeeForMeRepo  @Inject constructor(
    private val local: CommonDataSource,
    private val firebaseClient: FirebaseClient,
    private val fireStoreClient: FireStoreClient,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CommonRepository {

    override suspend fun getWorkflow(): Response<Workflow>? {
        return null
    }

    override suspend fun login(
        username: String,
        phone: String,
        status:String,
        isLogged: Boolean,
        token:String,
        done: (Boolean,Any?) -> Unit
    ): Boolean {

        return firebaseClient.login(username, phone,status,isLogged, token,done)
    }

    override suspend fun logout(
        username: String,
        phone: String,
        status:String,
        isLogged: Boolean,
        callback: (Any?) -> Unit
    ): Response<Workflow>? {
        firebaseClient.logout(username, phone,status,isLogged, callback)
        return null
    }

    override fun sendVerificationCode(activity:Activity,
        phone: String,
        phoneAuthCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        firebaseClient.sendVerificationCode( activity,phone,phoneAuthCallback)
    }


    override fun verifyCode(code: String):PhoneAuthCredential {
       return firebaseClient.verifyCode(code)
    }

    fun signInWithCredential(credential: PhoneAuthCredential, callback: (FirebaseUser?, Exception?) -> Unit) {
        firebaseClient.signInWithCredential(credential, callback)
    }

    fun setVerificationIdAndToken(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
       firebaseClient.setVerificationIdAndToken(verificationId, token)
    }

    fun addContactsToFirebase(addUserName: String, addPhoneNumber: String) {
        firebaseClient.addContactsToFirebase(addUserName, addPhoneNumber)
    }

}