package com.adv.ilook.model.util.responsehelper




enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val user_message:Any?,
    val is_loading:Boolean=false
){
    companion object{

        fun <T> success(data:T?): Resource<T> {
            return Resource(Status.SUCCESS, data, user_message=null)
        }

        fun <T> error(custom_message:Any="", data:T?): Resource<T> {
            return Resource(Status.ERROR, data, user_message=custom_message)
        }

        fun <T> loading(isLoading:Boolean=false,data:T?): Resource<T> {
            return Resource(Status.LOADING, data, user_message=null, is_loading = isLoading)
        }

    }
}