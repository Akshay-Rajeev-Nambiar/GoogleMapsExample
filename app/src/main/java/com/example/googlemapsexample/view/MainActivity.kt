package com.example.googlemapsexample.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import com.example.googlemapsexample.R


//My Main Activity

class MainActivity : AppCompatActivity() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest

    //Permission id
    private val PERMISSION_ID = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initiate Fused Location Provider Client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        map_btn.setOnClickListener {
            getLastLocation()
        }
    }

    //Function to get last location
    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLastLocation() {
        if(checkPermission()) {
            if(isLocationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener {task ->
                    var location = task.result
                    if(location == null) {
                        getNewLocation()
                    } else {
                        coordinates_tv.text = "Your current coordinates are :\nLat: " + location.latitude + " ; Long: " + location.longitude
                    }
                }
            } else {
                Toast.makeText(this, "Please enable your location service", Toast.LENGTH_LONG).show()
            }
        } else {
            RequestPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getNewLocation() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        fusedLocationProviderClient!!.requestLocationUpdates(
                locationRequest,locationCallback,Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        @SuppressLint("SetTextI18n")
        override fun onLocationResult(p0: LocationResult?) {
            val lastLocation = p0?.lastLocation
            if (lastLocation != null) {
                coordinates_tv.text = "Your current coordinates are :\nLat: " + lastLocation.latitude + " ; Long: " + lastLocation.longitude
                        }
        }
    }


    //Check user permission
    private fun checkPermission():Boolean  {
        if(
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        ) {
            return true
        }

        return false
    }
    //Get user permission
    private fun RequestPermission() {
        ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_ID
        )
    }
    //Check location service
    private fun isLocationEnabled():Boolean {
        var locationManager :LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    //Built in function to check permission result
    //Used it just for debugging
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Debug:", "You have the permission")
            }
        }
    }
}