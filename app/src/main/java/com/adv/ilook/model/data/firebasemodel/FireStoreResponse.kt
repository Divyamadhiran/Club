package com.adv.ilook.model.data.firebasemodel

import com.google.gson.annotations.SerializedName

data class FireStoreResponse(

	@field:SerializedName("device_id")
	val deviceId: String? = null,

	@field:SerializedName("phone_number")
	val phoneNumber: String? = null,

	@field:SerializedName("customer_name")
	val customerName: String? = null,

	@field:SerializedName("is_purchased")
	val isPurchased: Boolean? = null,

	@field:SerializedName("error")
    val errorMsg: String? = null
)
