package com.example.mainrobot.ui.settings

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mainrobot.databinding.FragmentSettingsBinding
import org.json.JSONObject

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var webView: WebView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)
        var frontCameraIp = ""
        var backCameraIp = ""
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



        val front_camera = "http://$frontCameraIp" // initial value
        val back_camera = "http://$backCameraIp" // initial value - if the car has two cameras

        var videoUrl = front_camera
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//        val textView: TextView = binding.textSettings
//        slideshowViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        val toggle: Switch = binding.switchButtonCamera
        webView = binding.webView
        webView.settings.javaScriptEnabled = true
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.settings.userAgentString =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.93 Safari/537.36"
        webView.webChromeClient = WebChromeClient()
        webView.loadUrl(videoUrl)

        toggle.setOnClickListener{
            if (videoUrl==front_camera){
                videoUrl = back_camera
                webView.loadUrl(videoUrl)
            }

            else if(videoUrl==back_camera) {
                videoUrl = front_camera
                webView.loadUrl(videoUrl)

            }
            else {

            }
            webView.loadUrl(videoUrl)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}