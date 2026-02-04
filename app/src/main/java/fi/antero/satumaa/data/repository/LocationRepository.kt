package fi.antero.satumaa.data.repository

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getCurrentLocation(): Flow<Location?>
    suspend fun getSingleLocation(): Location?
}
