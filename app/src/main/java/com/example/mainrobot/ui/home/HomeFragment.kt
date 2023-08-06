package com.example.mainrobot.ui.home

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mainrobot.databinding.FragmentHomeBinding
import com.example.mainrobot.RobocarViewModel
import com.example.mainrobot.ApiClient
import org.json.JSONObject

class HomeFragment : Fragment() {

    private lateinit var robocarViewModel: RobocarViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val apiClient = ApiClient()
    private val addressRobotCar = "http://192.168.2.45/api"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        var addressEditText: EditText = binding.inputEditText
        val saveConnect: Button = binding.saveConnect

        robocarViewModel = ViewModelProvider(requireActivity()).get(RobocarViewModel::class.java)

        var connect_status = ""
        val sharedConnect = context?.getSharedPreferences("MyConnect", Context.MODE_PRIVATE)

        //  check if the status is saved as connected. in that case shows the disconnect button
        if (sharedConnect != null){
            val jsonStringConnect = sharedConnect?.getString("jsonStringConnect", null)
            Log.d("HomeFragment", "jsonStringConnect: $jsonStringConnect")
            val jsonObject = jsonStringConnect?.let { JSONObject(it) }
            if (jsonObject != null) {
                connect_status = jsonObject.getString("connected")
                var address = jsonObject.getString("address")
                if (connect_status == "yes") {
                    Log.d("HomeFragment", "connect_status: $connect_status")
                    addressEditText.setText("$address")
                    saveConnect.text = "disconnect"
                }

            }
        }

        saveConnect.setOnClickListener {
            val address = addressEditText.text.toString()
            robocarViewModel.setRobocarAddress(address)

            // check if the file exist first to load the default values

            if (saveConnect.text == "connect"){

                apiClient.sendRequest(address, "GET") { response ->
                    // Handle the API response here
                    Log.d("RobotCarFragment", "API Response: $response")
                    activity?.runOnUiThread {
                        if (response != "") { // answer is received
                            saveConnect.text = "disconnect"

                            // Save the JSON string to SharedPreferences
                            val sharedPreferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                            //val jsonString = "{\"front_camera_ip\": \"192.168.2.186\", \"ap_wifi\": \"wifi\", \"back_camera_ip\": \"192.168.2.235\", \"ssid\": \"micasa\", \"password\": \"\"}"
                            val jsonString = response
                            sharedPreferences?.edit()?.putString("jsonString", jsonString)?.apply()

                            val jsonStringConnect = "{ 'connected':'yes', 'address' : '$address' }"
                            val sharedConnect = context?.getSharedPreferences("MyConnect", Context.MODE_PRIVATE)
                            sharedConnect?.edit()?.putString("jsonStringConnect", jsonStringConnect)?.apply()

                            Log.d("HomeFragment", "jsonStringConnect: $jsonStringConnect")
                            Log.d("HomeFragment", "Robocar connected at: $address")
                            Toast.makeText(requireContext(), "Robocar connected successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.d("HomeFragment", "Robocar did not connect at: $address")
                            Toast.makeText(requireContext(), "Robocar non connected", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }


            else if (saveConnect.text == "disconnect"){
                saveConnect.text = "connect"
                // Get the SharedPreferences instance
                val sharedConnect = context?.getSharedPreferences("MyConnect", Context.MODE_PRIVATE)

                // Get the existing JSON string from SharedPreferences
                val jsonStringConnect = sharedConnect?.getString("jsonStringConnect", null)

                //                change the status to connect = false
                if (jsonStringConnect != null) {
                    val jsonObject = JSONObject(jsonStringConnect)
                    jsonObject.put("connected", "no")
                    sharedConnect.edit().putString("jsonStringConnect", jsonObject.toString()).apply()

                }
                // Modify the "connected" value in the JSONObject

                Log.d("HomeFragment", "Robocar has been disconnected from: $address")
                Toast.makeText(requireContext(), "Robocar disconnected", Toast.LENGTH_SHORT).show()
            }


            else {

            }



        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
