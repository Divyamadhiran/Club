package com.adv.ilook.model.db.remote.firebase.firestore

import android.util.Log
import com.adv.ilook.model.util.assets.IPref
import com.adv.ilook.model.util.assets.PrefImpl
import com.adv.ilook.model.util.assets.SharedPrefKey
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import javax.inject.Inject

class FireStoreImpl @Inject constructor(db: FirebaseFirestore) : BaseFireStore(db) {
    private lateinit var lists: List<HashMap<String, Any?>>

    @Inject
    lateinit var sharedPref: PrefImpl

    suspend fun runAllFun(function: (jsonStr: String, docId: Any) -> Unit) {
        runAll() { jsonStr: String, docId: Any -> function(jsonStr, docId) }
    }

    suspend fun writeSelectRoomData(
        docPath: String = "",
        collectPath: String = "",
        arrayList: ArrayList<Any?>,
        function: (res: Any) -> Unit
    ) {
        Log.v(TAG, "writeSelectRoomData: ************* docPath =$docPath arrayList =$arrayList")
        val rooms = db.collection("gen_data")

        val map = hashMapOf("rooms" to arrayList)
        var userMail = sharedPref.str(SharedPrefKey.APP_USERMAIL)
        rooms.document("UsersRoom")
            .collection(userMail).document(docPath).set(map)
        // [END example_data]
    }

    suspend fun readSelectRoomData(
        docPath: String = "",
        collectPath: String = "",
        keys: String= SharedPrefKey.APP_AVAILABLE_ROOMS,
        function: (jsonStr: String, docId: Any?) -> Unit
    ) {
       try {
           var docListRooms: DocumentReference? = null

           val colGenData = db.collection("gen_data")
           val docUsersRoom = colGenData.document("UsersRoom")
           val userMail = sharedPref.str(SharedPrefKey.APP_USERMAIL)
           val colUserID = docUsersRoom.collection(userMail)
           if (keys == SharedPrefKey.APP_AVAILABLE_ROOMS) {
               Log.d(TAG, "readSelectRoomData: SharedPrefKey.APP_AVAILABLE_ROOMS")
               docListRooms = colUserID.document("list_of_rooms")
           } else if (keys == SharedPrefKey.APP_SELECTED_ROOM) {
               Log.d(TAG, "readSelectRoomData: SharedPrefKey.APP_SELECTED_ROOM")
               docListRooms = colUserID.document("selected_of_rooms")
           }
           val get = docListRooms?.get()

           get?.addOnSuccessListener { result ->

               val gson = Gson()
               var str = ""

               val gsonType: Type? = object : TypeToken<java.util.HashMap<*, *>?>() {}.type
               Log.i(TAG, "readSelectRoomData: id =${result.id} => map =${result.data}")
               str = gson.toJson(result.data, gsonType)
               // var genericModel = gson.fromJson(str, GenericModel::class.java)
               Log.d(TAG, "readSelectRoomData: Map to jsonStr = $str")

               function(str, result.id)

           }?.addOnFailureListener { exception ->

               Log.w(TAG, "Error getting documents.", exception)
               exception.message?.let { function(it, null) }
           }
       }catch (ex:Exception){
           ex.message?.let { function(it, null) }
       }
        // [END example_data]
    }

     suspend fun writeSelectedRoomsData(
        docPath: String="",
        collectPath: String="",
        keys: String="",
        arrayList: ArrayList<*>,
        isUpdate:Boolean=false,
        function: (res: Any) -> Unit
    ) {
         try {
         Log.w(TAG, "writeSelectedRoomsData: docPath =$docPath  == arrayList = $arrayList")
         var map: Map<String, Any>? = null
         when(arrayList.firstOrNull()){
             is Any ->{
                  map = hashMapOf("roomSelected" to arrayList)
                 val gson = Gson()
                 val gsonType: Type =object : TypeToken<java.util.HashMap<*, *>?>() {}.type
                 sharedPref.put(SharedPrefKey.APP_SELECTED_ROOM, gson.toJson(map, gsonType))
             }
             is Any ->{
                 map = hashMapOf("data" to arrayList)
                 val gson = Gson()
                 val gsonType: Type = object : TypeToken<java.util.HashMap<*, *>?>() {}.type
                 sharedPref.put(SharedPrefKey.APP_SELECTED_DEVICES, gson.toJson(map, gsonType))
             }
         }
         val colGenData = db.collection("gen_data")
         val docUsersRoom= colGenData.document("UsersRoom")
         val userMail= sharedPref.str(SharedPrefKey.APP_USERMAIL)
         val colUserID= docUsersRoom.collection(userMail)
         val doc =  colUserID.document(docPath)
         Log.v(TAG, "writeSelectedRoomsData: colUserID.document()  =${colUserID.document().path}")
         if (isUpdate){
             Log.d(TAG, "writeSelectedRoomsData: => doc.update")
             doc.update(map as Map<String, Any>)
         } else {
             Log.d(TAG, "writeSelectedRoomsData: -> doc.set")
             doc.set(map as Map<String, Any>)
         }
         colGenData.addSnapshotListener { value, error ->
            Log.d(TAG, "writeSelectedRoomsData() called with: value.isEmpty = ${value?.isEmpty}, error = $error")
        }

         }catch (ex:Exception){
             Log.e(TAG, "writeSelectedRoomsData: ${ex.message}")
             ex.message?.let { function( "") }
         }
    }

    suspend fun writeSwitchStatusUpdate(
        docPath: String="",
        collectPath: String="",
        keys: String="",
        arrayList: ArrayList<*>,
        isUpdate:Boolean=false,
        function: (res: Any) -> Unit
    ) {
        try {
            Log.w(TAG, "writeSwitchStatusUpdate: docPath =$docPath  == arrayList = $arrayList")
            var map: Map<String, Any>? = null
            when(arrayList.firstOrNull()){
                is Any ->{
                    map = hashMapOf("roomSelected" to arrayList)
                    val gson = Gson()
                    val gsonType: Type =
                        object : TypeToken<java.util.HashMap<*, *>?>() {}.type
                    sharedPref.put(SharedPrefKey.APP_SWITCH_STATUS, gson.toJson(map, gsonType))
                }
                is Any ->{
                    map = hashMapOf("data" to arrayList)
                    val gson = Gson()
                    val gsonType: Type =
                        object : TypeToken<java.util.HashMap<*, *>?>() {}.type
                    sharedPref.put(SharedPrefKey.APP_SWITCH_STATUS, gson.toJson(map, gsonType))
                }
            }
            val colGenData = db.collection("gen_data")
            val docUsersRoom= colGenData.document("UsersRoom")
            val userMail= sharedPref.str(SharedPrefKey.APP_USERMAIL)
            val colUserID= docUsersRoom.collection(userMail)
            val doc =  colUserID.document(docPath)
            //val docs= db.collection("gen_data").document("selected_of_rooms")
            Log.v(TAG, "writeSwitchStatusUpdate: colUserID.document()  =${colUserID.document().path}")
           if (isUpdate){
                Log.d(TAG, "writeSwitchStatusUpdate: => doc.update")
                doc.update(map as Map<String, Any>)
            } else {
                Log.d(TAG, "writeSwitchStatusUpdate: -> doc.set")
                doc.set(map as Map<String, Any>)
            }
            colGenData.addSnapshotListener { value, error ->
                Log.d(TAG, "writeSwitchStatusUpdate() called with: value.isEmpty = ${value?.isEmpty}, error = $error")
            }

        }catch (ex:Exception){
            Log.e(TAG, "writeSwitchStatusUpdate: ${ex.message}")
            ex.message?.let { function( "") }
        }
    }

    suspend fun getSelectedRoomsData(
        function: (jsonStr: String, genericModel: Any?) -> Unit
    ) {
        db.collection("gen_data")
            .document("selected_of_rooms")
            .get()
            .addOnSuccessListener { result ->
                var gson = Gson()
                var str = ""

                val gsonType: Type = object : TypeToken<java.util.HashMap<*, *>?>() {}.type
                Log.d(TAG, "getSelectedRoomsData:id =${result.id} => data =${result.data}")
                str = gson.toJson(result.data, gsonType)
                var genericModel = gson.fromJson(str, Any::class.java)
                function(str, genericModel)

            }
            .addOnFailureListener { exception ->

                Log.w(TAG, "Error getting documents.", exception)
                exception.message?.let { function(it, null) }
            }
    }


    // TODO: 08-01-2022 Firsttime From SignIn
    suspend fun getAvailableRoomsData(

        function: (jsonStr: String, roomsItem: Any?) -> Unit
    ) {
        db.collection("AvailableRooms")
            .get()
            .addOnSuccessListener { result ->
                var rooms = result.documentChanges.filter { change ->

                    val gson = Gson()
                    var str = ""

                    val gsonType: Type = object : TypeToken<java.util.HashMap<*, *>?>() {}.type
                    str = gson.toJson(change.document.data, gsonType)
                    val roomsItem = gson.fromJson(str, Any::class.java)

                    function(str, change.document.data)
                    true
                }
                // function("",rooms)


            }
            .addOnFailureListener { exception ->

                Log.w(TAG, "Error getting documents.", exception)
                exception.message?.let { function(it, null) }
            }

    }


}