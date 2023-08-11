package com.example.mainrobot.ui.locations

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.mainrobot.R
import com.example.mainrobot.databinding.FragmentLocationBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
class LocationFragment : Fragment() {

    companion object {
        fun newInstance() = LocationFragment()
    }

    private var _binding: FragmentLocationBinding? = null
    private lateinit var googleMap: GoogleMap
    private lateinit var editTextLatitude: EditText
    private lateinit var editTextLongitude: EditText
    private lateinit var viewModel: LocationViewModel

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val homeViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)

        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Get the SupportMapFragment and obtain the GoogleMap
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment

        mapFragment.getMapAsync { map ->
            googleMap = map

            // Enable zoom controls
            googleMap.uiSettings.isZoomControlsEnabled = true

            // Set initial map location (e.g., San Francisco)
            val initialLocation = LatLng(37.7749, -122.4194)
            val zoomLevel = 12.0f
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, zoomLevel))

            // Add a marker at the initial location (optional)
            googleMap.addMarker(
                MarkerOptions().position(initialLocation).title("Marker at San Francisco")
            )

            // Add an OnMapClickListener to the GoogleMap instance
            googleMap.setOnMapClickListener { latLng ->
                // When the map is clicked, update the EditText fields and recenter the map
                editTextLatitude.setText(latLng.latitude.toString())
                editTextLongitude.setText(latLng.longitude.toString())
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f))
                googleMap.clear() // Clear previous marker
                googleMap.addMarker(MarkerOptions().position(latLng).title("New Marker"))
            }
            mapFragment.requireView().parent.requestLayout()
        }

        editTextLatitude = root.findViewById(R.id.editTextLatitude)
        editTextLongitude = root.findViewById(R.id.editTextLongitude)

        // Listen for changes in the EditText fields and update the marker accordingly
        editTextLatitude.addTextChangedListener(textWatcher)
        editTextLongitude.addTextChangedListener(textWatcher)

        return root
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Not used
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Not used
        }

        override fun afterTextChanged(s: Editable?) {
            onTextChanged()
        }
    }

    private fun onTextChanged() {
        val latitude = editTextLatitude.text.toString().toDoubleOrNull()
        val longitude = editTextLongitude.text.toString().toDoubleOrNull()

        if (latitude != null && longitude != null) {
            val newLocation = LatLng(latitude, longitude)
            googleMap.clear() // Clear previous marker
            googleMap.addMarker(MarkerOptions().position(newLocation).title("New Marker"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 12.0f))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}