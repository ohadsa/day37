package gini.ohadsa.day35.location

import gini.ohadsa.day35.utils.SharedLocationManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class LocationRepository @Inject constructor(
        private val sharedLocationManager: SharedLocationManager
) {
    /**
     * Status of whether the app is actively subscribed to location changes.
     */
    val receivingLocationUpdates: StateFlow<Boolean> = sharedLocationManager.receivingLocationUpdates

    /**
     * Observable flow for location updates
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getLocations() = sharedLocationManager.locationFlow()
}