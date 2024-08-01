package com.adv.ilook.model.data.encrypt

import java.security.PrivateKey

data class EndToEnd(val token: String,val encrypt:String,val privateKey:PrivateKey)
