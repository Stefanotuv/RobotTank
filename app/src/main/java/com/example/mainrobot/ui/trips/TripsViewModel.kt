package com.example.mainrobot.ui.trips

import androidx.lifecycle.ViewModel

class TripsViewModel : ViewModel() {

    private fun getRecordedTrips(): List<RecordedTrip> {
        return listOf(
            RecordedTrip("Trip 1", "July 22, 2023", "2.5 km", "30 min", emptyList()),
            RecordedTrip("Trip 2", "July 23, 2023", "3.1 km", "45 min", emptyList()),
            RecordedTrip("Trip 3", "July 25, 2023", "4.2 km", "50 min", emptyList())
        )
    }

    fun fetchRecordedTrips(): List<RecordedTrip> {
        return getRecordedTrips()
    }

    // Other ViewModel functions and properties...
}