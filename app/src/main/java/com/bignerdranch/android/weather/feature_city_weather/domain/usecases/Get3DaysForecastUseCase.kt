package com.bignerdranch.android.weather.feature_city_weather.domain.usecases

import com.bignerdranch.android.weather.core.model.Result
import com.bignerdranch.android.weather.core.model.ShortForecastList
import com.bignerdranch.android.weather.feature_city_weather.domain.repository.CityWeatherRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.util.*
import java.util.Calendar.*

class Get3DaysForecastUseCase(
    private val cityWeatherRepository: CityWeatherRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(city: String): Flow<Result<ShortForecastList>> {
        return withContext(coroutineDispatcher) {
            val resultFlow = cityWeatherRepository.get3DaysForecast(city)
            var newFlow = resultFlow
            newFlow.collect { result ->
                if (result !is Result.Success) return@collect
                val shortForecast = result.data!!
                shortForecast.forecastDays.forEach { forecastDay ->
                    val day = forecastDay.date.toInt()
                    val today = GregorianCalendar().get(DAY_OF_MONTH)
                    when (day) {
                        today -> {
                            forecastDay.date = "Today"
                        }
                        GregorianCalendar().apply { add(DAY_OF_MONTH, 1) }.get(DAY_OF_MONTH) -> {
                            forecastDay.date = "Tomorrow"
                        }
                        else -> {
                            forecastDay.date = convertToWeekDay(day)
                        }
                    }
                }
                val sortedList = shortForecast.forecastDays.toMutableList().also { forecastDays ->
                    forecastDays.forEachIndexed { index, forecastDay ->
                        if (forecastDay.date == "Today" && index != 0) {
                            forecastDays.swap(index, 0)
                        } else if (forecastDay.date == "Tomorrow" && index != 1) {
                            forecastDays.swap(index, 1)
                        } else if (forecastDay.date !== "Tomorrow" && forecastDay.date == "Today" && index != 2) {
                            forecastDays.swap(index, 2)
                        }
                    }
                }
                newFlow =
                    flow { emit(Result.Success(ShortForecastList(forecastDays = sortedList))) }
            }
            return@withContext newFlow
        }
    }

    private fun convertToWeekDay(day: Int): String {
        val today = GregorianCalendar().get(DAY_OF_MONTH)
        val calendar = GregorianCalendar()
        calendar.set(DAY_OF_MONTH, day)
        return if(today>day) {
            calendar.add(MONTH, 1)
            calendar.get(DAY_OF_WEEK).weekDayNumberToString()
        } else calendar.get(DAY_OF_WEEK).weekDayNumberToString()
    }

    private fun Int.weekDayNumberToString() =
        when(this) {
            MONDAY -> "Monday"
            TUESDAY -> "Tuesday"
            WEDNESDAY -> "Wednesday"
            THURSDAY -> "Thursday"
            FRIDAY -> "Friday"
            SATURDAY -> "Saturday"
            SUNDAY -> "Sunday"
            else -> "Someday"
        }

    private fun <T> MutableList<T>.swap(first: Int, second: Int) {
        val secondObj = this[second]
        this[second] = this[first]
        this[first] = secondObj
    }
}