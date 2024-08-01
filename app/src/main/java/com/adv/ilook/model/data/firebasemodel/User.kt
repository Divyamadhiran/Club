package com.adv.ilook.model.data.firebasemodel

import com.google.firebase.database.IgnoreExtraProperties

// [START rtdb_user_class]
@IgnoreExtraProperties
data class User(val username: String? = null, val phone: String? = null) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}
// [END rtdb_user_class]
