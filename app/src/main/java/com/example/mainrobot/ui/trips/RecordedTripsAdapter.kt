package com.example.mainrobot.ui.trips

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.mainrobot.R



// RecordedTripsAdapter.kt
class RecordedTripsAdapter(private var recordedTrips: List<RecordedTrip>) :
    RecyclerView.Adapter<RecordedTripsAdapter.RecordedTripViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordedTripViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trip_banner, parent, false)



        // Set the width of the item to match the parent's width
        val layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
        view.layoutParams = layoutParams

        return RecordedTripViewHolder(view)
    }
    fun updateData(newData: List<RecordedTrip>) {
        recordedTrips = newData
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: RecordedTripViewHolder, position: Int) {
        val recordedTrip = recordedTrips[position]

        // Bind the trip details to the ViewHolder's views
        holder.tvTripName.text = recordedTrip.tripName
        holder.tvTripDate.text = "Date: ${recordedTrip.date}"
        holder.tvTripDistance.text = "Distance: ${recordedTrip.distance}"
        holder.tvTripDuration.text = "Duration: ${recordedTrip.duration}"

        // You can set the map image or any other map-related views here
        // For simplicity, let's use a placeholder image
        holder.ivMapImage.setImageResource(R.drawable.map_placeholder)

        // Handle click on the item to navigate to the details activity
        holder.itemView.setOnClickListener {
            val fragment = TripDetailsFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList("locationList", ArrayList(recordedTrip.locationList))
            fragment.arguments = bundle

            val fragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragment) // Replace with the actual RecyclerView ID
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
    override fun getItemCount(): Int {
        return recordedTrips.size
    }

    class RecordedTripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Existing views
        val ivMapImage: ImageView = itemView.findViewById(R.id.ivMapImage)
        val tvTripName: TextView = itemView.findViewById(R.id.tvTripName)
        val tvTripDate: TextView = itemView.findViewById(R.id.tvTripDate)
        val tvTripDistance: TextView = itemView.findViewById(R.id.tvTripDistance)
        val tvTripDuration: TextView = itemView.findViewById(R.id.tvTripDuration)
    }
}
