package com.adv.ilook.model.data.workflow

import kotlinx.serialization.Serializable

@Serializable
data class RemoteResponse(val status_code: Int, val status: Boolean, val message: String)