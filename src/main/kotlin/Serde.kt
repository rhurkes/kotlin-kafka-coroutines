@file:Suppress("unused")

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Serde {
    @ExperimentalSerializationApi
    @Suppress("ObjectPropertyName")
    val _instance: Json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @ExperimentalSerializationApi
    inline fun <reified T> encode(value: T) = _instance.encodeToString(value)

    @ExperimentalSerializationApi
    inline fun <reified T> decode(value: ByteArray) = _instance.decodeFromString<T>(String(value))

    @ExperimentalSerializationApi
    inline fun <reified T> decode(value: String) = _instance.decodeFromString<T>(value)
}
