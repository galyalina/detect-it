package com.detectit.model.location

data class DetectionLocation(val id: Int,
                             val latitude: Double,
                             val longitude: Double,
                             val description: String?,
                             val name: String?,
                             val path: String?,
                             val url: String?)