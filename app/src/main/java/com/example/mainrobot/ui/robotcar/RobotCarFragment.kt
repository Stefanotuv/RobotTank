package com.example.mainrobot.ui.robotcar

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mainrobot.ApiClient
import com.example.mainrobot.databinding.FragmentRobotcarBinding
import com.example.mainrobot.JoystickView
import com.example.mainrobot.RobocarViewModel
import org.json.JSONObject
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.widget.ImageButton;
import android.widget.Toast
import com.example.mainrobot.R
import java.util.Timer
import java.util.TimerTask

class RobotCarFragment : Fragment(), JoystickView.JoystickListener {

    private lateinit var binding: FragmentRobotcarBinding
    private lateinit var webView: WebView
    private lateinit var joystickTopCoordinates: TextView
    private lateinit var joystickBottomCoordinates: TextView
    private lateinit var distance: TextView

    private lateinit var switchButton: Switch
    private var videoUrl = "http://192.168.1.161:81/stream" // Replace with your IP camera video URL

    private val apiClient = ApiClient()
    private var addressRobotCar = "http://192.168.2.46/api"
    private lateinit var playRecordButton: ImageButton
    private lateinit var connectButton: ImageButton
    private var isRecording = false // Track recording state
    private var iSConnected = false // Track recording state



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("RobotCarFragment", "onCreateView")
        binding = FragmentRobotcarBinding.inflate(inflater, container, false)
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
        // Initialize the TextViews
        joystickTopCoordinates = binding.joystickTopCoordinates
        joystickBottomCoordinates = binding.joystickBottomCoordinates
        var frontCameraIp: String? = null
        var backCameraIp: String? = null

        val connectPref = context?.getSharedPreferences("MyConnect", Context.MODE_PRIVATE)
        if (connectPref != null){
            // the file exist so we can load the preferences

            val jsonStringConnect = connectPref?.getString("jsonStringConnect", null)
            if (jsonStringConnect != null) {
                val jsonObject = JSONObject(jsonStringConnect)
                val robotCarAddress = jsonObject.getString("address")
                addressRobotCar = "http://$robotCarAddress/api"
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
                backCameraIp = jsonObject.getString("back_camera_ip")

            }
        }





        // Initialize the Switch
        switchButton = binding.switchButton
        switchButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
//                videoUrl = "http://192.168.2.235:81/stream"
                videoUrl = frontCameraIp.toString()
            } else {
//                videoUrl = "http://192.168.2.235:81/stream"
                videoUrl = backCameraIp.toString()
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

        // Initialize the JoystickViews
        binding.joystickTop.setJoystickListener(object : JoystickView.JoystickListener {
            override fun onJoystickMoved(x: Float, y: Float) {
                handleJoystickMoved(x, y, "top")
            }

            override fun onJoystickReleased() {
                handleJoystickReleased("top")
            }
        })

        binding.joystickBottom.setJoystickListener(object : JoystickView.JoystickListener {
            override fun onJoystickMoved(x: Float, y: Float) {
                handleJoystickMoved(x, y, "bottom")
            }

            override fun onJoystickReleased() {
                handleJoystickReleased("bottom")
            }
        })

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
            Log.d("RobotCarFragment", "WebView load started")
            webView.loadUrl(videoUrl)
            Log.d("RobotCarFragment", "WebView load complete")
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
                Log.e("RobotCarFragment", "Error parsing distance JSON: ${e.message}")
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("RobotCarFragment", "onViewCreated")
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setJoystickPositions()
    }

    override fun onDestroyView() {

        super.onDestroyView()
        Log.d("RobotCarFragment", "onDestroyView")
        webView.destroy()
    }

    override fun onJoystickMoved(x: Float, y: Float) {
        // Not used for individual joysticks
    }

    override fun onJoystickReleased() {
        // Not used for individual joysticks
    }

    private var lastPostTime: Long = 0
    private val postThrottleInterval: Long = 250 // Throttle interval in milliseconds
    private fun handleJoystickMoved(x: Float, y: Float, joypad: String) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastPostTime >= postThrottleInterval) {
            val (scaledX, scaledY) = scaleCoordinate(x, y)

            if (joypad == "top") {
                joystickTopCoordinates.text = "X: ${String.format("%.2f", scaledX)}\nY: ${String.format("%.2f", scaledY)}"
            } else if (joypad == "bottom") {
                joystickBottomCoordinates.text = "X: ${String.format("%.2f", scaledX)}\nY: ${String.format("%.2f", scaledY)}"
            }

            val inputData = JSONObject().apply {
                put("scaledX", scaledX)
                put("scaledY", scaledY)
                put("joypad", joypad)

            }

            apiClient.sendRequest(addressRobotCar, "POST", inputData) { response ->
                // Handle the API response here
                Log.d("RobotCarFragment", "API Response: $response")
            }

            Log.d("RobotCarFragment", "Request POST sent: joypad=$joypad, scaledX=$scaledX, scaledY=$scaledY")

            lastPostTime = currentTime
        }
    }

    private fun handleJoystickReleased(joypad: String) {
        if (joypad == "top") {
            joystickTopCoordinates.text = "X: 0.00\nY: 0.00"
        } else if (joypad == "bottom") {
            joystickBottomCoordinates.text = "X: 0.00\nY: 0.00"
        }

        val inputData = JSONObject().apply {
            put("scaledX", 0)
            put("scaledY", 0)
            put("joypad", joypad)
        }

        apiClient.sendRequest(addressRobotCar, "POST", inputData) { response ->
            // Handle the API response here
            Log.d("RobotCarFragment", "API Response: $response")
        }

        Log.d("RobotCarFragment", "Request POST sent: joypad=$joypad, scaledX=0, scaledY=0")
    }

    private fun scaleCoordinate(x: Float, y: Float): Pair<Float, Float> {
        val angle = Math.atan2(y.toDouble(), x.toDouble()).toFloat()
        val radius = 1.0f
        val scaledX = radius * Math.cos(angle.toDouble()).toFloat()
        val scaledY = radius * Math.sin(angle.toDouble()).toFloat()
        return Pair(scaledX, -scaledY)
    }

    private fun setJoystickPositions() {
        Log.d("RobotCarFragment", "setJoystickPositions")
        val layoutParamsTop = binding.joystickTop.layoutParams as ViewGroup.LayoutParams
        val layoutParamsBottom = binding.joystickBottom.layoutParams as ViewGroup.LayoutParams

        val displayMetrics = resources.displayMetrics
        val joystickSize = (displayMetrics.widthPixels * 0.3).toInt()

        layoutParamsTop.width = joystickSize
        layoutParamsTop.height = joystickSize

        layoutParamsBottom.width = joystickSize
        layoutParamsBottom.height = joystickSize
    }
}
