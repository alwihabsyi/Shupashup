package com.alwihabsyi.shupashup.util

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
){
    class Success<T>(data: T): Resource<T>(data)
    class Error<T>(message: String): Resource<T>(message = message)
    class Loading<T>: Resource<T>()

//    object Loading: Resource<Nothing>()
//    data class Success<out T>(val data: T) : Resource<T>()
//    data class Error(val message: String?) : Resource<Nothing>()
//    class Unspecified : Resource<List<Nothing>>() {
//
//    }
}
