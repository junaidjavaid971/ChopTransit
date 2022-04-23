package app.com.choptransit.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import app.com.choptransit.R
import app.com.choptransit.models.observer.UserLocation
import app.com.choptransit.models.response.DriverData
import app.com.choptransit.utilities.Commons
import app.com.choptransit.utilities.SharePrefData
import app.com.choptransit.viewmodels.DriverViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase


open class BaseActivity : AppCompatActivity() {
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    lateinit var driverViewModel: DriverViewModel

    private var mHandler: Handler? = null
    private val PERMISSIONS_REQUEST_CODE: Int = 321545

    var driverData: DriverData = DriverData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        driverViewModel = ViewModelProvider(this)[DriverViewModel::class.java]
        if (SharePrefData.getInstance().containsPrefData(this, "driver")) {
            driverData = SharePrefData.getInstance().getDriverData(this, "driver")
        }
        mHandler = Handler(Looper.getMainLooper()!!)

        getCurrentLocation()
        observe()

    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
            return
        }
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0 ?: return
                for (location in p0.locations) {
                    if (location == null) {
                        return
                    }
                    val currentLatLng = LatLng(location.latitude, location.longitude)

                    Log.d(
                        "onDriverLocationChanged",
                        currentLatLng.latitude.toString() + " - " + currentLatLng.longitude
                    )

                    val userLocation = UserLocation()
                    userLocation.latitude = currentLatLng.latitude
                    userLocation.longitude = currentLatLng.longitude

                    if (driverData.signedIn.equals("1")) {
                        if (driverData.userID != null || driverData.userID != "") {
                            val reference =
                                FirebaseDatabase.getInstance().getReference("DriverLocations")
                                    .child(driverData.userID)
                            reference.setValue(userLocation)
                        }
                    }
                }
            }
        }

        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(
            mLocationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun observe() {
        driverViewModel.locationLiveData.observe(this) {
            Log.d("LOCUPD", "Location Updated")
        }
    }

    private fun requestPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            getCurrentLocation()
        }
    }
}