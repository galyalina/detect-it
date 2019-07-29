package com.detectit.model.location

class Mapper {
    companion object {
        fun toDetectionLocation(source: PointOfInterestEntity) =
                DetectionLocation(
                        id = source.id,
                        latitude = source.latitude,
                        longitude = source.longitude,
                        description = source.description,
                        name = source.name,
                        path = source.path,
                        url = source.url)
    }
}