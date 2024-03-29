package com.bignerdranch.android.weather.core.extensions

import androidx.compose.ui.geometry.Offset
import java.util.*

fun Int.putInRange(min: Int, max: Int) =
    if(this in min..max) this
    else if(this < min) min
    else max


fun Double.toIntIfPossible(): String {
    if(this == this.toInt().toDouble()) {
        return this.toInt().toString()
    }
    return this.toString()
}

fun Offset.normalized(): Offset {
    val length = this.getDistance()
    return Offset(this.x / length, this.y / length)
}

operator fun Offset.times(number: Number) = Offset(number.toFloat()*x, number.toFloat()*y)

fun dayName(day: Int): String {
    val today = GregorianCalendar().get(Calendar.DAY_OF_MONTH)
    return when (day) {
        today -> {
            "Today"
        }
        GregorianCalendar().apply { add(Calendar.DAY_OF_MONTH, 1) }.get(Calendar.DAY_OF_MONTH) -> {
            "Tomorrow"
        }
        else -> {
            convertToWeekDay(day)
        }
    }
}

fun convertToWeekDay(day: Int, shortened: Boolean = false): String {
    val today = GregorianCalendar().get(Calendar.DAY_OF_MONTH)
    val calendar = GregorianCalendar()
    calendar.set(Calendar.DAY_OF_MONTH, day)
    return if(today>day) {
        calendar.add(Calendar.MONTH, 1)
        calendar.get(Calendar.DAY_OF_WEEK).weekDayNumberToString(shortened)
    } else calendar.get(Calendar.DAY_OF_WEEK).weekDayNumberToString(shortened)
}

fun Int.weekDayNumberToString(shortened: Boolean) =
    when(shortened) {
        true -> {
            when(this) {
                Calendar.MONDAY -> "Monday"
                Calendar.TUESDAY -> "Tuesday"
                Calendar.WEDNESDAY -> "Wednesday"
                Calendar.THURSDAY -> "Thursday"
                Calendar.FRIDAY -> "Friday"
                Calendar.SATURDAY -> "Saturday"
                Calendar.SUNDAY -> "Sunday"
                else -> "Someday"
            }
        }
        false -> {
            when(this) {
                Calendar.MONDAY -> "Mon"
                Calendar.TUESDAY -> "Tue"
                Calendar.WEDNESDAY -> "Wed"
                Calendar.THURSDAY -> "Thu"
                Calendar.FRIDAY -> "Fri"
                Calendar.SATURDAY -> "Sat"
                Calendar.SUNDAY -> "Sun"
                else -> "Someday"
            }
        }
    }