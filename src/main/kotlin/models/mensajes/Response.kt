package models.mensajes

import kotlinx.serialization.Serializable

@Serializable
data class Response<T>(
    val content: T?,
    val type: Type
){
    enum class Type {
        OK, ERROR
    }
}