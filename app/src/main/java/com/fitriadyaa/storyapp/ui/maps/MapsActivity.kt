package com.fitriadyaa.storyapp.ui.maps

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.fitriadyaa.storyapp.R
import com.fitriadyaa.storyapp.data.remote.response.storyResponse.Story
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.fitriadyaa.storyapp.databinding.ActivityMapsBinding
import com.fitriadyaa.storyapp.utils.ViewModelFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.fitriadyaa.storyapp.data.Result
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.tasks.Task
import java.util.concurrent.TimeUnit
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri


private val tag = MapsActivity::class.java.simpleName
private const val REQUEST_LOCATION_PERMISSION = 1
private const val REQUEST_CHECK_SETTINGS = 2
private lateinit var locationRequest: LocationRequest
private lateinit var locationCallback: LocationCallback

@Suppress("DEPRECATION")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val boundsBuilder = LatLngBounds.Builder()
    private val mapViewModel: MapsViewModel by viewModels {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressBar = binding.progressBar
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        configureMapSettings()
        observeStories()

        getMyLastLocation()
        setMapStyle()
        createLocationRequest()
        createLocationCallback()
        startLocationUpdates()
        mMap.setOnMarkerClickListener(this)
    }

    private var listStoryItems: List<Story> = emptyList()

    private fun showStoryDetail(story: Story) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.card_item_detail, null)

        val ivStory = dialogView.findViewById<ImageView>(R.id.iv_story)
        val tvName = dialogView.findViewById<TextView>(R.id.tv_detail_name)
        val tvDescription = dialogView.findViewById<TextView>(R.id.tv_desc)

        Glide.with(ivStory)
            .load(story.photoUrl)
            .into(ivStory)

        tvName.text = story.name
        tvDescription.text = story.description

        val dialog = dialogBuilder.setView(dialogView)
            .create()

        val btnOK = dialogView.findViewById<Button>(R.id.btn_ok)
        btnOK.setOnClickListener {
            dialog.dismiss()
        }

        val btnShowDirection = dialogView.findViewById<Button>(R.id.btn_show_direction)
        btnShowDirection.setOnClickListener {
            showDirectionOnMaps(story.lat, story.lon)
        }

        dialog.show()
    }

    private fun showDirectionOnMaps(storyLatitude: Double, storyLongitude: Double) {
        val gmmIntentUri = Uri.parse("google.navigation:q=$storyLatitude,$storyLongitude&mode=d")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        try {
            startActivity(mapIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Google Maps not installed.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        if (listStoryItems.isNotEmpty()) {
            val selectedStoryItem = listStoryItems.find { it.name == marker.title }
            selectedStoryItem?.let {
                showStoryDetail(it)
            }
        } else {
            Log.e(tag, "No stories found in the list.")
        }
        return true
    }


    private fun configureMapSettings() {
        with(mMap.uiSettings) {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
            isMyLocationButtonEnabled = true
        }
    }

    private fun observeStories() {
        progressBar.visibility = View.VISIBLE
        mapViewModel.getStoriesWithLocation().observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    addManyMarkers(result.data.listStory)
                    progressBar.visibility = View.GONE
                }
                is Result.Error -> {
                    showToast("Error: ${result.error}")
                    progressBar.visibility = View.GONE
                }
                is Result.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun addManyMarkers(stories: List<Story>) {
        if (stories.isNotEmpty()) {
            listStoryItems = stories
            stories.forEach { story ->
                val latLng = LatLng(story.lat, story.lon)
                mMap.addMarker(MarkerOptions().position(latLng).title(story.name))
                boundsBuilder.include(latLng)
            }
            val bounds: LatLngBounds = boundsBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    30
                )
            )
        } else {
            Log.e(tag, "No stories found in the list.")
            // You can show an appropriate error message to the user here
        }
    }


    private fun checkPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    mMap.isMyLocationEnabled = true
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                } else {
                    Toast.makeText(
                        this@MapsActivity,
                        getString(R.string.location_not_found),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun setMapStyle() {
        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this,
                    R.raw.map_style
                )
            )
            if (!success) {
                Log.e(tag, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(tag, "Can't find style. Error: ", e)
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = TimeUnit.SECONDS.toMillis(1)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            getMyLastLocation()
        }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(this@MapsActivity, REQUEST_CHECK_SETTINGS)
                } catch (_: IntentSender.SendIntentException) {
                }
            }
        }
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getMyLastLocation()
                } else {
                    Toast.makeText(this, getString(R.string.permission), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
