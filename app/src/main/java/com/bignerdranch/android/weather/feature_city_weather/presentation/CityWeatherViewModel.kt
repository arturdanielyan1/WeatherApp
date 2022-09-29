package com.bignerdranch.android.weather.feature_city_weather.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.weather.core.ARG_CITY
import com.bignerdranch.android.weather.core.log
import com.bignerdranch.android.weather.core.model.Result
import com.bignerdranch.android.weather.feature_city_weather.domain.usecases.Get3DaysForecastUseCase
import com.bignerdranch.android.weather.feature_city_weather.domain.usecases.GetCityWeatherUseCase
import com.bignerdranch.android.weather.feature_city_weather.domain.usecases.GetIconUseCase
import com.bignerdranch.android.weather.feature_city_weather.presentation.state_wrappers.CityWeatherState
import com.bignerdranch.android.weather.feature_city_weather.presentation.state_wrappers.ShortForecastState
import com.bignerdranch.android.weather.feature_city_weather.presentation.state_wrappers.WeatherIconState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityWeatherViewModel @Inject constructor(
    private val getCityWeatherUseCase: GetCityWeatherUseCase,
    private val get3DaysForecastUseCase: Get3DaysForecastUseCase,
    private val getIconUseCase: GetIconUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _currentWeatherState = MutableStateFlow(CityWeatherState())
    val currentWeatherState = _currentWeatherState.asStateFlow()

    private val _weatherIcon = MutableStateFlow(WeatherIconState())
    val weatherIcon = _weatherIcon.asStateFlow()

    private val _shortForecastState = MutableStateFlow(ShortForecastState())
    val shortForecastState = _shortForecastState.asStateFlow()

    init {
        savedStateHandle.get<String>(ARG_CITY)?.let {
            getCityWeather(it)
        }
    }

    fun getCityWeather(city: String) {
        _shortForecastState.value = ShortForecastState() // makes progress bar appear for short forecast cards
        viewModelScope.launch {
            getCityWeatherUseCase(city).collect { result ->
                when(result) {
                    is Result.Success -> {
                        _currentWeatherState.value = CityWeatherState(
                            isLoading = false,
                            cityWeather = result.data!!,
                            error = ""
                        )
                        getIcon()
                        get3DayShortWeather(city)
                    }
                    is Result.Loading -> {
                        _currentWeatherState.value = CityWeatherState(
                            isLoading = true,
                            cityWeather = null,
                            error = ""
                        )
                    }
                    is Result.Error -> {
                        _currentWeatherState.value = CityWeatherState(
                            isLoading = false,
                            cityWeather = null,
                            error = result.message!!
                        )
                    }
                }
            }
        }
    }

    private fun get3DayShortWeather(city: String) {
        viewModelScope.launch {
            get3DaysForecastUseCase(city).collect { result ->
                when(result) {
                    is Result.Loading -> {
                        _shortForecastState.value = ShortForecastState(
                            isLoading = true,
                            shortForecast = null,
                            error = ""
                        )
                    }
                    is Result.Success -> {
                        _shortForecastState.value = ShortForecastState(
                            isLoading = false,
                            shortForecast = result.data!!,
                            error = ""
                        )
                    }
                    is Result.Error -> {
                        _shortForecastState.value = ShortForecastState(
                            isLoading = false,
                            shortForecast = null,
                            error = result.message!!
                        )
                    }
                }
            }
        }
    }

    private fun getIcon() {
        viewModelScope.launch {
            log("getting icon")
            _weatherIcon.value = WeatherIconState(getIconUseCase())
        }
    }
}