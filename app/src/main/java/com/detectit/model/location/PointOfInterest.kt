package com.detectit.model.location

import io.requery.*

@Entity
interface PointOfInterest : Persistable {
    @get:Key
    @get:Generated
    val id: Int

    val latitude: Double
    val longitude: Double
    val description: String
    val name: String
    val path: String
    val url: String
}