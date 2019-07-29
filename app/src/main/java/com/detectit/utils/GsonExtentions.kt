package com.detectit.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement

/**
 * Gson Kotlin Extension Functions.
 */

fun Any.toJson(): String = Gson().toJson(this)

fun Any.toJsonTree(): JsonElement = Gson().toJsonTree(this)

fun Any.toJsonTreeWithNulls(): JsonElement =
        GsonBuilder().serializeNulls().create().toJsonTree(this)

fun Any.toJsonWithNulls(): String = GsonBuilder().serializeNulls().create().toJson(this)

fun Any.toPrettyJson(): String =
        GsonBuilder().serializeNulls().setPrettyPrinting().create().toJson(this)

fun Any.toPrettyJsonWithNulls(): String =
        GsonBuilder().serializeNulls().setPrettyPrinting().create().toJson(this)

fun Any.toJsonWithStaticFields(): String = GsonBuilder().excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT).create().toJson(this)
