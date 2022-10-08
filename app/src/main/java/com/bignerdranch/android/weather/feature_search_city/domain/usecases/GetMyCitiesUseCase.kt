package com.bignerdranch.android.weather.feature_search_city.domain.usecases

import com.bignerdranch.android.weather.feature_search_city.data.repository.SearchCityRepositoryImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GetMyCitiesUseCase(
    private val repository: SearchCityRepositoryImpl,
    private val coroutineDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke() =
        withContext(coroutineDispatcher) {
            repository.getCities().map { result ->
                result.sortedBy { it.city }
            }
        }
}