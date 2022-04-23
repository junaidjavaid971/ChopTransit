package app.com.choptransit.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.com.choptransit.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions

class DragableMapsFragment(
    callback: MapLocationChangedCallback,
    receivedLatitude: Double,
    receivedLongitude: Double
) : Fragment() {

    lateinit var map: GoogleMap
    private val callback = OnMapReadyCallback { googleMap ->
        this.map = googleMap

        val selectedLocation = LatLng(receivedLatitude, receivedLongitude)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(selectedLocation))
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireActivity(),
                R.raw.mapstyle
            )
        )
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 20f))

        map.setOnCameraIdleListener {
            val midLatLng = map.cameraPosition.target
            callback.onMapLocationChangedCallback(midLatLng.latitude, midLatLng.longitude)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dragable_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

    }


    interface MapLocationChangedCallback {
        fun onMapLocationChangedCallback(latitude: Double, longitude: Double)
    }
}