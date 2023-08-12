package com.example.mainrobot.ui.trips

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mainrobot.R
class TripsFragment : Fragment() {

    private lateinit var viewModel: TripsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var tripsAdapter: RecordedTripsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trips, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        tripsAdapter = RecordedTripsAdapter(emptyList()) // Initialize with empty list for now
        recyclerView.adapter = tripsAdapter


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TripsViewModel::class.java)

        val recordedTrips = viewModel.fetchRecordedTrips() // Fetch the recorded trips
        tripsAdapter.updateData(recordedTrips) // Update the adapter with the recorded trips
    }
}
