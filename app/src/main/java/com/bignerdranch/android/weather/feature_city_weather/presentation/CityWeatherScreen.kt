package com.bignerdranch.android.weather.feature_city_weather.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.bignerdranch.android.weather.core.extensions.toIntIfPossible
import com.bignerdranch.android.weather.core.log
import com.bignerdranch.android.weather.feature_city_weather.presentation.components.ExtremePointsWeatherCard
import com.bignerdranch.android.weather.ui.theme.defaultGradientEnd
import com.bignerdranch.android.weather.ui.theme.defaultGradientStart
import kotlin.math.roundToInt

//@Preview(showBackground = true)
//@Composable
//fun CityWeatherScreenPreview() {
//    WeatherTheme {
//        Surface(
//            modifier = Modifier
//                .fillMaxSize(),
//            color = MaterialTheme.colors.background
//        ) {
//            CityWeatherScreen("")
//        }
//    }
//}

const val ADD_BUTTON_ID = "add_button"
const val CITY_TEXT_ID = "city_text"
const val REFRESH_BUTTON_ID = "refresh_button_id"
const val COUNTRY_TEXT_ID = "country_button_id"

@Composable
fun CityWeatherScreen(
    viewModel: CityWeatherViewModel
) {
//    var launchedEffectTriggered by rememberSaveable {  mutableStateOf(false) }
//    if(!launchedEffectTriggered)
//    LaunchedEffect(key1 = Unit) {
//        viewModel.getCityWeather(city)
//        launchedEffectTriggered = true
//        log("vm called")
//    }

    val weatherState = viewModel.currentWeatherState.value

    val constraintsTopBar = ConstraintSet {
        val addButton = createRefFor(ADD_BUTTON_ID)
        val cityText = createRefFor(CITY_TEXT_ID)
        val refreshButton = createRefFor(REFRESH_BUTTON_ID)
        val countryText = createRefFor(COUNTRY_TEXT_ID)

        constrain(addButton) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
        }

        constrain(cityText) {
            top.linkTo(parent.top)
            bottom.linkTo(countryText.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }

        constrain(countryText) {
            top.linkTo(cityText.bottom)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }

        constrain(refreshButton) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end)
        }
    }

    Box(
        modifier = Modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(MaterialTheme.colors.defaultGradientStart.toArgb()),
                        Color(MaterialTheme.colors.defaultGradientEnd.toArgb())
                    ),
                    start = Offset.Zero,
                    end = Offset.Infinite
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row( // top bar row
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth(),
                    constraintSet = constraintsTopBar
                ) {
                    Icon(
                        modifier = Modifier
                            .layoutId(ADD_BUTTON_ID),
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add city"
                    )
                    Text(
                        modifier = Modifier
                            .layoutId(CITY_TEXT_ID),
                        text = weatherState.cityWeather?.city ?: "Loading…",
                        style = MaterialTheme.typography.h4
                    )
                    Text(
                        modifier = Modifier
                            .layoutId(COUNTRY_TEXT_ID)
                            .padding(top = 8.dp),
                        text = weatherState.cityWeather?.country ?: "Loading…",
                        fontSize = 15.sp
                    )
                    Icon(
                        modifier = Modifier
                            .layoutId(REFRESH_BUTTON_ID),
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reload"
                    )
                }
            }
//            LaunchedEffect(Unit) {
//                val calendar = GregorianCalendar()
//                log("${calendar.get(YEAR)} ${calendar.get(MONTH)+1} ${calendar.get(DAY_OF_MONTH)}  ${calendar.get(
//                    HOUR_OF_DAY)}:${calendar.get(MINUTE)}:${calendar.get(SECOND)} ${calendar.get(DAY_OF_WEEK_FIELD)}")
//            }
            Column( // temp and desc column
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if(weatherState.cityWeather != null) {
                    Spacer(Modifier.height(36.dp))
                    Text(
                        text = "${weatherState.cityWeather.tempInCelsius.toIntIfPossible()}°C",
                        style = MaterialTheme.typography.h1
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        var descTextHeight by remember { mutableStateOf(0) }
                        Text(
                            text = weatherState.cityWeather.description,
                            fontSize = 25.sp,
                            modifier = Modifier
                                .onGloballyPositioned {
                                    descTextHeight = it.boundsInWindow().size.height.roundToInt()
                                }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        if(viewModel.weatherIcon.value.icon != null)
                            Image(
                                bitmap = viewModel.weatherIcon.value.icon!!.asImageBitmap(),
                                contentDescription = weatherState.cityWeather.description,
                                modifier = Modifier
                                    .height((descTextHeight / LocalDensity.current.density).dp - 8.dp),
                                contentScale = ContentScale.Crop
                            )
                        else
                            CircularProgressIndicator(
                                color = Color(0xFF1F3C88),
                                modifier = Modifier
                                    .size((descTextHeight / LocalDensity.current.density).dp - 8.dp),
                                strokeWidth = 3.dp
                            )
                    }
                }
            }
            Spacer(modifier = Modifier.height(36.dp))
            val shortForecastState by viewModel.shortForecastState.collectAsState()
            if(shortForecastState.shortForecast != null) {
                val shortForecast = shortForecastState.shortForecast!!
                Column( // 3 days column
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    ExtremePointsWeatherCard(
                        minTemp = shortForecast.forecastDays[0].minTempInCelsius,
                        maxTemp = shortForecast.forecastDays[0].maxTempInCelsius,
                        description = shortForecast.forecastDays[0].description,
                        day = shortForecast.forecastDays[0].day
                    )
                    ExtremePointsWeatherCard(
                        minTemp = shortForecast.forecastDays[1].minTempInCelsius,
                        maxTemp = shortForecast.forecastDays[1].maxTempInCelsius,
                        description = shortForecast.forecastDays[1].description,
                        day = shortForecast.forecastDays[1].day
                    )
                    ExtremePointsWeatherCard(
                        minTemp = shortForecast.forecastDays[2].minTempInCelsius,
                        maxTemp = shortForecast.forecastDays[2].maxTempInCelsius,
                        description = shortForecast.forecastDays[2].description,
                        day = shortForecast.forecastDays[2].day
                    )
                }
            }
        }
    }
}