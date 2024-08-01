package com.adv.ilook.model.db.remote.firebase.realtimedatabase

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import javax.inject.Inject

private const val TAG = "RealTimeDBImpl"
class RealTimeDBImpl @Inject constructor(databaseReference: DatabaseReference) :
    BaseRealTimeDataBase(databaseReference) {

    private lateinit var messagesRef: DatabaseReference
    private lateinit var databaseReference: DatabaseReference
    private lateinit var messagesQuery: Query

    private lateinit var messagesListener: ValueEventListener
    private lateinit var messagesQueryListener: ChildEventListener


    var uid: String = "42"

    private fun getMultipleInstances() {
        // [START rtdb_multi_instance]
        // Get the default database instance for an app
        val primary = Firebase.database.reference

        // Get a secondary database instance by URL
        val secondary = Firebase.database("https://testapp-1234.firebaseio.com").reference
        // [END rtdb_multi_instance]
    }

    fun initDB() {
        databaseReference = Firebase.database.reference
    }


    fun writeNewDeviceConfigureWithTaskListeners(
        childMap: HashMap<String, Any>,
        dataMap: HashMap<String, Any> = mutableMapOf<String,Any>() as HashMap<String, Any>,
        childList: ArrayList<Any?> = emptyList<Any>() as ArrayList<Any?>,
        type: String,
        function: (res: Any) -> Unit
    ) {
        val resMap = hashMapOf<Int, Any>()
        val resList = ArrayList<Any>()
        // val user = User(name, email)
        when (type) {
            "1" -> {
                messagesRef = database.child("app").child(childMap["child2"] as String)
                    .child(childMap["child3"] as String).child(childMap["child4"] as String).child(childMap["child5"] as String)
                messagesRef.updateChildren(dataMap)
                    .addOnSuccessListener {
                        messagesListener = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                Log.d(TAG, "Number of messages: ${dataSnapshot.childrenCount}")
                                dataSnapshot.children.forEach { child ->
                                    child.key?.let { key ->
                                        child.value?.let { value ->
                                            resMap[key.toInt()] = value
                                        }
                                    }
                                }
                                val gson = Gson()
                                var str = ""
                                val gsonType: Type =
                                    object : TypeToken<java.util.HashMap<*, *>?>() {}.type
                                str = gson.toJson(resMap, gsonType)
                                function(str)
                            }
                            override fun onCancelled(error: DatabaseError) {
                                // Could not successfully listen for data, log the error
                                Log.e(TAG, "messages:onCancelled: ${error.message}")
                            }
                        }
                        messagesRef.addValueEventListener(messagesListener)
                    }
                    .addOnFailureListener {
                        it.message?.let { ex -> function(ex) }
                    }

            }
            "2" -> {
                val data = dataMap["data"]
                when (data) {
                    is String -> {
                        function("")
                    }
                    else -> {
                        function("Invalid data type")
                    }
                }
            }
            "3" -> {
                messagesRef = database.child("app")
                    .child(childMap["child2"] as String)
                    .child(childMap["child3"] as String)
                    .child(childMap["child4"] as String)
                   // .child(childMap["child5"].toString())
                messagesRef .setValue(childList)
                    .addOnSuccessListener {
                        messagesListener = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                Log.d(TAG, "Number of messages: ${dataSnapshot.childrenCount}")
                                Log.d(TAG, "Number of data: ${dataSnapshot.children.filter {
                                    Log.d(TAG, "onDataChange: key = ${it.key} || value = ${it.value} ")
                                    true }}")
                                dataSnapshot.children.forEach { child ->
                                    child.key?.let { key ->
                                        child.value?.let { value ->
                                          //  resMap[key.toInt()] = value
                                            resList.add(value)
                                        }

                                    }
                                }
                                val gson = Gson()
                                var str = ""
                                val gsonType: Type =
                                    object : TypeToken<java.util.ArrayList<*>?>() {}.type
                                str = gson.toJson(resList, gsonType)
                                function(str)
                              //  function("response complete")
                            }
                            override fun onCancelled(error: DatabaseError) {
                                // Could not successfully listen for data, log the error
                                Log.e(TAG, "messages:onCancelled: ${error.message}")
                            }
                        }
                        messagesRef.addValueEventListener(messagesListener)
                    }
                    .addOnFailureListener {
                        it.message?.let { ex -> function(ex) }
                    }
            }
        }
    }

    fun addSingleValueEventListener(){
        messagesRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun writeDeviceSwitchStatusWithTaskListeners(
        childMap: HashMap<String, Any>,
        dataMap: HashMap<String, Any> = mutableMapOf<String,Any>() as HashMap<String, Any>,
        childList: ArrayList<Any?> = emptyList<Any>() as ArrayList<Any?>,
        type: String,
        function: (res: Any) -> Unit
    ) {
        val resMap = hashMapOf<Int, Any>()
        val resList = ArrayList<Any>()
        // val user = User(name, email)
        when (type) {
            "1" -> {
                messagesRef = database.child("app").child(childMap["child2"] as String)
                    .child(childMap["child3"] as String).child(childMap["child4"] as String).child(childMap["child5"] as String)
                messagesRef.updateChildren(dataMap)
                    .addOnSuccessListener {
                        messagesListener = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                Log.d(TAG, "Number of messages: ${dataSnapshot.childrenCount}")
                                dataSnapshot.children.forEach { child ->
                                    child.key?.let { key ->
                                        child.value?.let { value ->
                                            resMap[key.toInt()] = value
                                        }
                                    }
                                }
                                val gson = Gson()
                                var str = ""
                                val gsonType: Type =
                                    object : TypeToken<java.util.HashMap<*, *>?>() {}.type
                                str = gson.toJson(resMap, gsonType)
                                function(str)
                            }
                            override fun onCancelled(error: DatabaseError) {
                                // Could not successfully listen for data, log the error
                                Log.e(TAG, "messages:onCancelled: ${error.message}")
                            }
                        }
                        messagesRef.addValueEventListener(messagesListener)
                    }
                    .addOnFailureListener {
                        it.message?.let { ex -> function(ex) }
                    }
            }
            "2" -> {
                val data = dataMap["data"]
                when (data) {
                    is String -> {
                        function("")
                    }
                    else -> {
                        function("Invalid data type")
                    }
                }
            }
            "3" -> {
                messagesRef = database.child("app")
                    .child(childMap["child2"] as String)
                    .child(childMap["child3"] as String)
                    .child(childMap["child4"] as String)
                // .child(childMap["child5"].toString())
                messagesRef .setValue(childList)
                    .addOnSuccessListener {
                        messagesListener = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                Log.d(TAG, "Number of messages: ${dataSnapshot.childrenCount}")
                                Log.d(TAG, "Number of data: ${dataSnapshot.children.filter {
                                    Log.d(TAG, "onDataChange: key = ${it.key} || value = ${it.value} ")
                                    true }}")
                                dataSnapshot.children.forEach { child ->
                                    child.key?.let { key ->
                                        child.value?.let { value ->
                                            //  resMap[key.toInt()] = value
                                            resList.add(value)
                                        }

                                    }
                                }
                                val gson = Gson()
                                var str = ""
                                val gsonType: Type =
                                    object : TypeToken<java.util.ArrayList<*>?>() {}.type
                                str = gson.toJson(resList, gsonType)
                                function(str)
                                //  function("response complete")
                            }
                            override fun onCancelled(error: DatabaseError) {
                                // Could not successfully listen for data, log the error
                                Log.e(TAG, "messages:onCancelled: ${error.message}")
                            }
                        }
                        messagesRef.addValueEventListener(messagesListener)
                    }
                    .addOnFailureListener {
                        it.message?.let { ex -> function(ex) }
                    }
            }
        }
    }

    fun readNewDeviceConfigureWithTaskListeners( childMap: HashMap<String, Any>, function: (res: Any) -> Unit){
        val resMap = hashMapOf<String, Any>()
       val messagesRef = database.child("app").child(childMap["child2"] as String)
            .child(childMap["child3"] as String).child(childMap["child4"] as String).child(childMap["child5"] as String)

      val  messagesListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "readNewDeviceConfigureWithTaskListeners : Number of messages: ${dataSnapshot.childrenCount}")
                dataSnapshot.children.forEach { child ->
                    child.key?.let { key ->
                        child.value?.let { value ->
                            resMap.put(
                                key,
                                value
                            )
                        }
                    }
                }
                val gson = Gson()
                var str = ""
                val gsonType: Type =
                    object : TypeToken<java.util.HashMap<*, *>?>() {}.type
                str = gson.toJson(resMap, gsonType)
                function(str)
            }
            override fun onCancelled(error: DatabaseError) {
                // Could not successfully listen for data, log the error
                Log.e(TAG, "messages:onCancelled: ${error.message}")
            }
        }
        messagesRef.addValueEventListener(messagesListener)
    }
    private fun basicListen() {
        // [START basic_listen]
        // Get a reference to Messages and attach a listener
        messagesRef = databaseReference.child("messages")
        messagesListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // New data at this path. This method will be called after every change in the
                // data at this path or a subpath.

                Log.d(TAG, "Number of messages: ${dataSnapshot.childrenCount}")
                dataSnapshot.children.forEach { child ->
                    // Extract Message object from the DataSnapshot
                  //  val message: Message? = child.getValue<Message>()

                    // Use the message
                    // [START_EXCLUDE]
                  //  Log.d(TAG, "message text: ${message?.text}")
                   // Log.d(TAG, "message sender name: ${message?.name}")
                    // [END_EXCLUDE]
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Could not successfully listen for data, log the error
                Log.e(TAG, "messages:onCancelled: ${error.message}")
            }
        }
        messagesRef.addValueEventListener(messagesListener)
        // [END basic_listen]
    }


    private fun basicQuery() {
        // [START basic_query]
        // My top posts by number of stars
        val myUserId = uid
        val myTopPostsQuery = databaseReference.child("user-posts").child(myUserId)
            .orderByChild("starCount")

        myTopPostsQuery.addChildEventListener(object : ChildEventListener {
            // TODO: implement the ChildEventListener methods as documented above
            // [START_EXCLUDE]
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
            // [END_EXCLUDE]
        })
        // [END basic_query]
    }

    private fun basicQueryValueListener() {
        val myUserId = uid
        val myTopPostsQuery = databaseReference.child("user-posts").child(myUserId)
            .orderByChild("starCount")

        // [START basic_query_value_listener]
        // My top posts by number of stars
        myTopPostsQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    // TODO: handle the post
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })
        // [END basic_query_value_listener]
    }

    private fun cleanBasicListener() {
        // Clean up value listener
        // [START clean_basic_listen]
        messagesRef.removeEventListener(messagesListener)
        // [END clean_basic_listen]
    }

    private fun cleanBasicQuery() {
        // Clean up query listener
        // [START clean_basic_query]
        messagesQuery.removeEventListener(messagesQueryListener)
        // [END clean_basic_query]
    }

    fun orderByNested() {
        // [START rtdb_order_by_nested]
        // Most viewed posts
        val myMostViewedPostsQuery = databaseReference.child("posts")
            .orderByChild("metrics/views")
        myMostViewedPostsQuery.addChildEventListener(object : ChildEventListener {
            // TODO: implement the ChildEventListener methods as documented above
            // [START_EXCLUDE]
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {}

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}

            override fun onCancelled(databaseError: DatabaseError) {}
            // [END_EXCLUDE]
        })
        // [END rtdb_order_by_nested]
    }

    private fun childEventListenerRecycler() {
        val context = this
        // [START child_event_listener_recycler]
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)

                // A new comment has been added, add it to the displayed list
            //    val comment = dataSnapshot.getValue<Comment>()

                // ...
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
             //   val newComment = dataSnapshot.getValue<Comment>()
                val commentKey = dataSnapshot.key

                // ...
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                val commentKey = dataSnapshot.key

                // ...
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.key!!)

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
              //  val movedComment = dataSnapshot.getValue<Comment>()
                val commentKey = dataSnapshot.key

                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException())
                /* Toast.makeText(context, "Failed to load comments.",
                     Toast.LENGTH_SHORT).show()*/
            }
        }
        databaseReference.addChildEventListener(childEventListener)
        // [END child_event_listener_recycler]
    }

    private fun recentPostsQuery() {
        // [START recent_posts_query]
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys.
        databaseReference.child("posts").limitToFirst(100)
        // [END recent_posts_query]
    }


}