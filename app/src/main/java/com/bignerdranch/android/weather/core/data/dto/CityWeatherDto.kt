package com.bignerdranch.android.weather.core.data.dto

import com.bignerdranch.android.weather.core.data.model.ShortWeatherInfo
import com.google.gson.annotations.SerializedName

fun CityWeatherDto.toShortWeatherInfo() =
    ShortWeatherInfo(
        city = location.city,
        country = location.country,
        tempInCelsius = current.tempInCelsius,
        tempInFahrenheit = current.tempInFahrenheit,
        latitude = lat,
        longitude = lon
    )
data class CityWeatherDto(
    @SerializedName("location") val location: Location,
    @SerializedName("current") val current: Current,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double
)

data class Location(
    @SerializedName("name") val city: String,
    @SerializedName("country") val country: String,
)

data class Current(
    @SerializedName("temp_c") val tempInCelsius: Double,
    @SerializedName("temp_f") val tempInFahrenheit: Double,
    @SerializedName("is_day") val isDay: Int,
    @SerializedName("condition") val condition: ConditionDto,
    @SerializedName("pressure_mb") val pressure: Int
)