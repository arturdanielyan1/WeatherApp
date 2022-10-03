package com.bignerdranch.android.weather.core.data.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bignerdranch.android.weather.core.data.room.db_schemas.MyCitiesDbSchema
import com.bignerdranch.android.weather.feature_search_city.domain.model.ShortWeatherInfo

@Dao
interface MyCitiesDao {

    @Query("SELECT * FROM ${MyCitiesDbSchema.TABLE_NAME}")
    suspend fun getSavedCities(): List<ShortWeatherInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ShortWeatherInfo)
}