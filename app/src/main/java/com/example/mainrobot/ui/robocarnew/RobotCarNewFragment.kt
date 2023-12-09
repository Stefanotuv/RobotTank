package com.example.mainrobot.ui.robocarnew

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.PorterDuff
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.mainrobot.ApiClient
import com.example.mainrobot.JoystickView
import com.example.mainrobot.R
//import com.example.mainrobot.databinding.FragmentRobotcarBinding
import com.example.mainrobot.databinding.FragmentRobotCarNewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Timer
import java.util.TimerTask

class RobotCarNewFragment : Fragment() {

    private lateinit var binding: FragmentRobotCarNewBinding
    private lateinit var webView: WebView
    private lateinit var distance: TextView

    private lateinit var switchButton: Switch
    private var videoUrl = "http://192.168.1.126:81/stream" // Replace with your IP camera video URL

    private val apiClient = ApiClient()
    private var addressRobotCar = "http://192.168.2.46/api"
    private lateinit var playRecordButton: ImageButton
    private lateinit var connectButton: ImageButton
    private var isRecording = false // Track recording state
    private var iSConnected = false // Track recording state

    private fun getViewNameFromId(viewId: Int): String {
        return when (viewId) {
            R.id.triangleUp_up -> "triangleUp_up"
            R.id.triangleCircle_up -> "triangleCircle_up"
            R.id.triangleDown_up -> "triangleDown_up"
            R.id.triangleLeft_up -> "triangleLeft_up"
            R.id.triangleRight_up -> "triangleRight_up"

            R.id.triangleUp_down -> "triangleUp_down"
            R.id.triangleCircle_down -> "triangleCircle_down"
            R.id.triangleDown_down -> "triangleDown_down"
            R.id.triangleLeft_down -> "triangleLeft_down"
            R.id.triangleRight_down -> "triangleRight_down"

            // Add more cases for other views

            else -> "Unknown"
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onImageViewClicked(imageView: Int) {

        val imageView = binding.root.findViewById<ImageView>(imageView)
        // Save the original color

        val clickedViewId: Int = imageView.id
        val viewName = getViewNameFromId(clickedViewId)

        // Handle the view name, for example, log it
        Log.d("ClickedViewName", viewName)
        var (value, control)= extractComponents(viewName)


        var inputData = JSONObject().apply {
            put("control", control)
            put("value", value)
        }

        Log.d("RobotCarNewFragment", "Control: $control")
        Log.d("RobotCarNewFragment", "Value: $value")

        imageView.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d("ImageViewClicked", "ACTION_DOWN")
                    // Change the color when the button is pressed
                    (view as ImageView).setColorFilter(
                        ContextCompat.getColor(requireContext(), R.color.white),
                        PorterDuff.Mode.SRC_IN
                    )
                    Log.d("RobotCarNewFragment", "API Control: $control")
                    Log.d("RobotCarNewFragment", "API Value: $value")
                    val apiClient = ApiClient()
                    val address_robotcar = "$addressRobotCar/api/controls"
                    apiClient.sendRequest(address_robotcar, "POST", inputData) { response ->
                        // Handle the API response here

                        Log.d("RobotCarNewFragment", "API Response: $response")
                        activity?.runOnUiThread {
                            if (response != "") { // answer is received

                                Log.d("RobotCarNewFragment", "api command sent succesfully")
//                                Toast.makeText(requireContext(), "Configuration saved succesfully", Toast.LENGTH_SHORT).show()

                            } else {
                                Log.d("RobotCarNewFragment", "api command not received")
//                                Toast.makeText(requireContext(), "Configuration NOT saved", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }


                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    Log.d("RobotCarNewFragment_ImageViewClicked", "ACTION_UP or ACTION_CANCEL")
                    // Revert to the original color when the button is released or canceled
                    (view as ImageView).clearColorFilter()
                    Log.d("RobotCarNewFragment", "API Control: $control")
                    Log.d("RobotCarNewFragment", "API Value: $value")

                    // Handle the click action
                    // api call
                    // the stop is need only for the motors as the camera are step servos
                    if(control == "down"){
                        val apiClient = ApiClient()
                        val address_robotcar = "$addressRobotCar/api/controls"


                        var stopData = JSONObject().apply {
                            put("control", control)
                            put("value", value)
                        }

                        stopData.put("value", "stop")

                        apiClient.sendRequest(address_robotcar, "POST", stopData) { response ->
                            // Handle the API response here

                            Log.d("RobotCarNewFragment", "API Response: $response")
                            activity?.runOnUiThread {
                                if (response != "") { // answer is received

                                    Log.d("RobotCarNewFragment", "api command sent succesfully")
//                                Toast.makeText(requireContext(), "Configuration saved succesfully", Toast.LENGTH_SHORT).show()

                                } else {
                                    Log.d("RobotCarNewFragment", "api command not received")
//                                Toast.makeText(requireContext(), "Configuration NOT saved", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
            true // Return true to consume the event
        }
        imageView.setOnClickListener {
            // Your click handling logic here
            Log.d("RobotCarNewFragment_ImageViewClicked", "Clicked")
            // This block will be executed when the ImageView is clicked
        }
    }

    fun extractComponents(viewName: String): Pair<String, String> {
        val parts = viewName.removePrefix("triangle").split("_")
        if (parts.size == 2) {
            return Pair(parts[0], parts[1])
        } else {
            // Handle the case where the string format is not as expected
            return Pair("Unknown", "Unknown")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("RobotCarNewFragment", "onCreateView")
        binding = FragmentRobotCarNewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize the WebView
        webView = binding.webView
        webView.settings.javaScriptEnabled = true
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.settings.userAgentString =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.93 Safari/537.36"
        webView.webChromeClient = WebChromeClient()
//        webView.loadUrl(videoUrl)
        launchWebView(videoUrl)

        // Get references to your image views
        val triangleUpUp = binding.root.findViewById<ImageView>(R.id.triangleUp_up)
        val triangleCircleUp = binding.root.findViewById<ImageView>(R.id.triangleCircle_up)
        val triangleDownUp = binding.root.findViewById<ImageView>(R.id.triangleDown_up)
        val triangleLeftUp = binding.root.findViewById<ImageView>(R.id.triangleLeft_up)
        val triangleRightUp = binding.root.findViewById<ImageView>(R.id.triangleRight_up)

        val triangleUpDown = binding.root.findViewById<ImageView>(R.id.triangleUp_down)
        val triangleCircleDown = binding.root.findViewById<ImageView>(R.id.triangleCircle_down)
        val triangleDownDown = binding.root.findViewById<ImageView>(R.id.triangleDown_down)
        val triangleLeftDown = binding.root.findViewById<ImageView>(R.id.triangleLeft_down)
        val triangleRightDown = binding.root.findViewById<ImageView>(R.id.triangleRight_down)

        // Set click listeners for the image views
        triangleUpUp.setOnClickListener { onImageViewClicked(R.id.triangleUp_up) }
        triangleCircleUp.setOnClickListener { onImageViewClicked(R.id.triangleCircle_up) }
        triangleDownUp.setOnClickListener { onImageViewClicked(R.id.triangleDown_up) }
        triangleLeftUp.setOnClickListener { onImageViewClicked(R.id.triangleLeft_up) }
        triangleRightUp.setOnClickListener { onImageViewClicked(R.id.triangleRight_up) }

        triangleUpDown.setOnClickListener { onImageViewClicked(R.id.triangleUp_down) }
        triangleCircleDown.setOnClickListener { onImageViewClicked(R.id.triangleCircle_down) }
        triangleDownDown.setOnClickListener { onImageViewClicked(R.id.triangleDown_down) }
        triangleLeftDown.setOnClickListener { onImageViewClicked(R.id.triangleLeft_down) }
        triangleRightDown.setOnClickListener { onImageViewClicked(R.id.triangleRight_down) }



        // Initialize the TextViews
//        joystickTopCoordinates = binding.joystickTopCoordinates
//        joystickBottomCoordinates = binding.joystickBottomCoordinates
        var frontCameraIp: String? = null
        var backCameraIp: String? = null

        val connectPref = context?.getSharedPreferences("MyConnect", Context.MODE_PRIVATE)
        if (connectPref != null){
            // the file exist so we can load the preferences

            val jsonStringConnect = connectPref?.getString("jsonStringConnect", null)
            if (jsonStringConnect != null) {
                val jsonObject = JSONObject(jsonStringConnect)
                val robotCarAddress = jsonObject.getString("address")
                addressRobotCar = "http://$robotCarAddress"
            }
        }

        val sharedPreferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        if (sharedPreferences != null){
            // the file exist so we can load the preferences
            val jsonString = sharedPreferences?.getString("jsonString", null)
            Log.d("ConfigurationsFragment", "jsonString: $jsonString")
            if(jsonString!= null) {
                val jsonObject = JSONObject(jsonString)
                frontCameraIp = jsonObject.getString("front_camera_ip")
                videoUrl = "http://$frontCameraIp:81/stream"
                backCameraIp = jsonObject.getString("back_camera_ip")

            }
        }


        // Initialize the Switch
        switchButton = binding.switchButton
        switchButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
//                videoUrl = "http://192.168.2.235:81/stream"
                var backCameraIpString = backCameraIp.toString()
                videoUrl = "$backCameraIpString:81/stream"
            } else {

//                videoUrl = "http://192.168.2.235:81/stream"
                var frontCameraIpString = frontCameraIp.toString()
                videoUrl = "$frontCameraIpString:81/stream"
            }
            Log.d("RobotCarFragment", "Switch value changed: $isChecked")
            webView.loadUrl(videoUrl) // Update the video URL in the WebView
        }

        // Initialize the playRecordButton
        playRecordButton = binding.playRecordButton
        playRecordButton.setOnClickListener {
            if (isRecording) {
                // Stop recording
                Toast.makeText(requireContext(), "Recording Stopped", Toast.LENGTH_SHORT).show()
                playRecordButton.setImageResource(R.drawable.ic_rec)
                // Perform stop recording functionality
            } else {
                // Start recording
                Toast.makeText(requireContext(), "Start Recording", Toast.LENGTH_SHORT).show()
                playRecordButton.setImageResource(R.drawable.ic_stop)
                // Perform start recording functionality
            }
            isRecording = !isRecording // Toggle recording state
        }

        // Initialize the connectButton
        connectButton = binding.connectButton
        connectButton.setOnClickListener {
            if (iSConnected) {
                // Disconnect
                Toast.makeText(requireContext(), "disconnecting", Toast.LENGTH_SHORT).show()
                connectButton.setImageResource(R.drawable.disconnected)



                // Perform stop recording functionality
            } else {
                // Start recording
                Toast.makeText(requireContext(), "connecting", Toast.LENGTH_SHORT).show()
                connectButton.setImageResource(R.drawable.connected)
                // Perform start recording functionality
            }
            iSConnected = !iSConnected // Toggle recording state
        }


        // Initialize the distance
        distance = binding.textDistance
        fetchDistanceFromServer()
        // Use a Timer or any other mechanism to periodically update the distance
        val updateIntervalMillis = 500 // Update every 5 seconds, you can adjust this interval
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                fetchDistanceFromServer()
            }
        }, updateIntervalMillis.toLong(), updateIntervalMillis.toLong())

        return root
    }
    private fun launchWebView(videoUrl: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            Log.d("RobotCarNewFragment", "WebView load started")
            webView.loadUrl(videoUrl)
            Log.d("RobotCarNewFragment", "WebView load complete")
        }
    }

    private fun fetchDistanceFromServer() {
        var addressRobotCar = ""
        val connectPref = context?.getSharedPreferences("MyConnect", Context.MODE_PRIVATE)
        if (connectPref != null){
            // the file exist so we can load the preferences

            val jsonStringConnect = connectPref?.getString("jsonStringConnect", null)
            if (jsonStringConnect != null) {
                val jsonObject = JSONObject(jsonStringConnect)
                addressRobotCar = jsonObject.getString("address")
            }
        }



        val apiUrl = "http://$addressRobotCar/api/distance" // Replace with your actual API endpoint for distance retrieval

        apiClient.sendRequest(apiUrl, "GET") { response ->
            try {
                val jsonResponse = JSONObject(response)
                val distanceValue = jsonResponse.optDouble("distance", 0.0)
                val formattedDistance = String.format("%.2f", distanceValue)

                activity?.runOnUiThread {
                    distance.text = "Distance: $formattedDistance" // Update the distance TextView
                }
            } catch (e: Exception) {
                Log.e("RobotCarNewFragment", "Error parsing distance JSON: ${e.message}")
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("RobotCarFragment", "onViewCreated")
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//        setJoystickPositions()
    }

    override fun onDestroyView() {

        super.onDestroyView()
        Log.d("RobotCarNewFragment", "onDestroyView")
        webView.destroy()
    }


    private var lastPostTime: Long = 0
    private val postThrottleInterval: Long = 250 // Throttle interval in milliseconds

    private fun scaleCoordinate(x: Float, y: Float): Pair<Float, Float> {
        val angle = Math.atan2(y.toDouble(), x.toDouble()).toFloat()
        val radius = 1.0f
        val scaledX = radius * Math.cos(angle.toDouble()).toFloat()
        val scaledY = radius * Math.sin(angle.toDouble()).toFloat()
        return Pair(scaledX, -scaledY)
    }

}
