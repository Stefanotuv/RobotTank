package com.example.mainrobot.ui.trips

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mainrobot.R

class TripDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trip_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("TripDetailsFragment", "onViewCreated: Fragment created")

        // Handle the logic for displaying the trip details and map
        // You can use your existing code from TripDetailsActivity here

        try {
            // Your map setup code here
            // For example: val mapFragment = childFragmentManager.findFragmentById(R.id.tripMapFragment) as SupportMapFragment
        } catch (e: Exception) {
            Log.e("TripDetailsFragment", "Map setup error: ${e.message}")
        }
    }
}

