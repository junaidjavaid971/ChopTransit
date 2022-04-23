package app.com.choptransit.app.com.choptransit.models.request

import java.io.Serializable

data class StopRequestModel(
    val lvl: Int,
    val id: String,
    val stopName: String,
    val longitude: Double,
    val latitude: Double
) : Serializable
