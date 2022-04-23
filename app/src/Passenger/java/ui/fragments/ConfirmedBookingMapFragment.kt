package ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.com.choptransit.R
import app.com.choptransit.models.observer.UserLocation
import app.com.choptransit.models.response.AssignRouteData
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.DriverViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


open class ConfirmedBookingMapFragment(var route: AssignRouteData) : Fragment() {
    var zoomCompleted = false
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var mapFragment: SupportMapFragment? = null
    var mMap: GoogleMap? = null

    var routeData: AssignRouteData? = null
    lateinit var currentLoc: LatLng

    lateinit var viewModel: DriverViewModel

    var myMarker: Marker? = null
    var driverMarker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        viewModel = ViewModelProvider(this)[DriverViewModel::class.java]

        getDriverLocation()

        if (mapFragment != null) {
            mapFragment!!.getMapAsync(callback)
        }

        requestPermission()
    }

    private val callback = OnMapReadyCallback { googleMap: GoogleMap ->
        mMap = googleMap
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireActivity(),
                R.raw.mapstyle
            )
        )
        getCurrentLocation()
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ),
            44
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 44) {
            getCurrentLocation()
        }
    }

    private fun getDriverLocation() {
        val reference = FirebaseDatabase.getInstance().getReference("DriverLocations")
            .child(routeData?.bus?.driver?.userID.toString())
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userLocation = snapshot.getValue(UserLocation::class.java)
                if (userLocation != null) {
                    val latLng = LatLng(userLocation.latitude, userLocation.longitude)

                    if (userLocation.latitude == null || userLocation.longitude == null) {
                        return
                    }
                    val icon = Commons.drawableToBitmap(
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.ic_bus_small
                        )
                    )
                    val markerOptions = MarkerOptions().position(latLng).icon(
                        BitmapDescriptorFactory.fromBitmap(icon)
                    )
                    if (driverMarker != null) {
                        driverMarker?.remove()
                    }
                    driverMarker = mMap?.addMarker(markerOptions)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ERROR", error.message)
            }

        })
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireActivity(),
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
                        "onLocationChanged",
                        currentLatLng.latitude.toString() + " - " + currentLatLng.longitude
                    )
                    currentLoc = currentLatLng
                    mMap?.uiSettings?.isMyLocationButtonEnabled = true
                    mMap?.uiSettings?.isZoomControlsEnabled = true
                    val icon = Commons.drawableToBitmap(
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.ic_dot
                        )
                    )
                    val markerOptions = MarkerOptions().position(currentLoc).icon(
                        BitmapDescriptorFactory.fromBitmap(icon)
                    )

                    if (!zoomCompleted) {
                        mMap?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLatLng,
                                15f
                            )
                        )
                        zoomCompleted = true
                    }

                    if (myMarker != null) {
                        myMarker?.remove()
                    }
                    myMarker = mMap?.addMarker(markerOptions)!!

                }
            }
        }

        LocationServices.getFusedLocationProviderClient(requireActivity()).requestLocationUpdates(
            mLocationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
}