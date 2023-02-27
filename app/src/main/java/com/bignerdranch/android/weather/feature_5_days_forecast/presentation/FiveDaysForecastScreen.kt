package com.bignerdranch.android.weather.feature_5_days_forecast.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.bignerdranch.android.weather.core.log
import com.bignerdranch.android.weather.feature_5_days_forecast.presentation.components.DayInfoCard

@OptIn(ExperimentalTextApi::class)
@Composable
fun FiveDaysForecastScreen(
    viewModel: FiveDaysForecastViewModel
) {
    val forecastState = viewModel.fiveDaysForecastState.collectAsState()
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "5-day forecast",
            style = MaterialTheme.typography.h4,
            color = Color.White,
            modifier = Modifier
                .padding(36.dp)
        )
        if (forecastState.value.list != null) {
            val extremePoints = forecastState.value.list!!.forecastDays
            val maxTemp by remember {
                var max = Double.MIN_VALUE
                extremePoints.forEach {
                    if (it.maxTempInCelsius > max) {
                        max = it.maxTempInCelsius
                    }
                }
                mutableStateOf(max)
            }
            val minTemp by remember {
                var min = Double.MAX_VALUE
                extremePoints.forEach {
                    if (it.minTempInCelsius < min) {
                        min = it.minTempInCelsius
                    }
                }
                mutableStateOf(min)
            }


            val canvasHeight by remember { mutableStateOf(200) }
            val unitCount by remember { mutableStateOf(maxTemp - minTemp) }
            val unitHeight by remember { mutableStateOf(canvasHeight / unitCount) }
            val circleRadius by remember { mutableStateOf(5) }
            val graphStroke by remember { mutableStateOf(2) }
            var cardWidth by remember { mutableStateOf(0f) }
            val verticalPadding by remember { mutableStateOf(50) }
            val graphPadding by remember { mutableStateOf(30) }
            val textMeasurer = rememberTextMeasurer()


            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 36.dp)
                    .onGloballyPositioned {
                        cardWidth = it.boundsInWindow().width / 5
                    },
                horizontalArrangement = Arrangement.Center
            ) {
                val days = forecastState.value.list!!.forecastDays
                log(days.size)
                days.forEachIndexed { index, weatherInfo ->
                    DayInfoCard(
                        weatherInfo = weatherInfo,
                        nextDayWeatherInfo = try { days[index+1] } catch (e: IndexOutOfBoundsException){null},
                        previousDayWeatherInfo = try { days[index-1] } catch (e: IndexOutOfBoundsException){null},
                        canvasHeight = canvasHeight,
                        unitHeight = unitHeight,
                        circleRadius = circleRadius,
                        graphStroke = graphStroke,
                        cardWidth = cardWidth,
                        minTemp = minTemp
                    )
                }
            }

            /*
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .background(MaterialTheme.colors.background)
            ) {
                val days = forecastState.value.list!!.forecastDays
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 36.dp
                        )
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in days.indices) {
                        DayInfo(
                            date = days[i].date,
                            icon = days[i].icon
                        )
                    }
                }
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(canvasHeight.dp + verticalPadding.dp * 2)
                ) {
                    val maxPoints: MutableList<Offset> = mutableListOf()
                    val minPoints: MutableList<Offset> = mutableListOf()
                    extremePoints.forEachIndexed { index, extremePoints ->
                        val graphPaddingPx = graphPadding.dp.toPx()
                        val x =
                            index * ((this.size.width - 2 * graphPaddingPx) / 4) + graphPaddingPx

                        // drawing max temp circles
                        val fromMin = extremePoints.maxTempInCelsius - minTemp
                        val yFromMin = (fromMin * unitHeight).dp.toPx()
                        val y = canvasHeight.dp.toPx() - yFromMin + verticalPadding.dp.toPx()
                        drawCircle(
                            center = Offset(x, y),
                            radius = circleRadius.dp.toPx(),
                            color = Color.White,
                            style = Stroke(
                                width = circleStroke.dp.toPx()
                            )
                        )
                        drawText(
                            textMeasurer = textMeasurer,
                            text = "${extremePoints.maxTempInCelsius.toIntIfPossible()}°",
                            topLeft = Offset(
                                (x - 15.dp.toPx()),
                                y - 36.dp.toPx()
                            ),
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 17.sp
                            ),
                            overflow = TextOverflow.Visible,
                            softWrap = false
                        )
                        // drawing min temp circles
                        val fromMin2 = extremePoints.minTempInCelsius.roundToInt() - minTemp
                        val yFromMin2 = (fromMin2 * unitHeight).dp.toPx()
                        val y2 = canvasHeight.dp.toPx() - yFromMin2 + verticalPadding.dp.toPx()
                        drawCircle(
                            center = Offset(x, y2),
                            radius = circleRadius.dp.toPx(),
                            color = Color.White,
                            style = Stroke(
                                width = circleStroke.dp.toPx()
                            )
                        )
                        drawText(
                            textMeasurer = textMeasurer,
                            text = "${extremePoints.minTempInCelsius.toIntIfPossible()}°",
                            topLeft = Offset(
                                (x - 15.dp.toPx()),
                                y2 + 10.dp.toPx()
                            ),
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 17.sp
                            ),
                            overflow = TextOverflow.Visible,
                            softWrap = false
                        )
                        maxPoints.add(Offset(x, y))
                        minPoints.add(Offset(x, y2))
                    }
                    for (i in 0..minPoints.size - 2) {
                        drawLine(
                            start = minPoints[i] + ((minPoints[i + 1] - minPoints[i]).normalized() * (circleRadius.dp.toPx())),
                            end = minPoints[i + 1] + ((minPoints[i] - minPoints[i + 1]).normalized() * (circleRadius.dp.toPx())),
                            color = Color.White,
                            strokeWidth = 2.dp.toPx()
                        )
                        drawLine(
                            start = maxPoints[i] + ((maxPoints[i + 1] - maxPoints[i]).normalized() * (circleRadius.dp.toPx())),
                            end = maxPoints[i + 1] + ((maxPoints[i] - maxPoints[i + 1]).normalized() * (circleRadius.dp.toPx())),
                            color = Color.White,
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                }
            }*/
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}