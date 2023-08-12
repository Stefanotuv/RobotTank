package com.example.mainrobot.ui.trips

// RecordedTrip.kt

import com.google.android.gms.maps.model.LatLng

data class RecordedTrip(
    val tripName: String,
    val date: String,
    val distance: String,
    val duration: String,
    val locationList: List<LatLng> // Add the locationList property here
)

