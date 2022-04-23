package ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import app.com.choptransit.R
import app.com.choptransit.databinding.ActivityAddStopsBinding
import app.com.choptransit.models.response.Stop
import app.com.choptransit.models.response.StopsResponse
import app.com.choptransit.ui.DragableMapActivity
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.AdminViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.*


class AddStopsActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddStopsBinding
    lateinit var viewModel: AdminViewModel
    lateinit var mapFragment: SupportMapFragment
    val AUTOCOMPLETE_REQUEST_CODE = 123451
    lateinit var stop: Stop
    lateinit var place: Place

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_stops)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AdminViewModel::class.java)
        binding.addStopViewModel = viewModel

        mapFragment =
            (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!

        val apiKey = getString(R.string.str_APIKEY)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        if (intent.hasExtra("stop")) {
            stop = intent.getSerializableExtra("stop") as Stop
            setupStop()
        }

        setUpActionbar()
        setObservers()
        manageClicks()
    }

    private fun setupStop() {
        binding.btnAddStop.text = getString(R.string.str_updateStop)
        (binding.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.str_updateStop)
        viewModel.stopModel.stopName = stop.stopName
        viewModel.stopModel.latitude = stop.latitude.toString().toDouble()
        viewModel.stopModel.longitude = stop.longitude.toString().toDouble()
        viewModel.stopModel.id = stop.id
        viewModel.stopModel.updateStop = true

        val latlng = LatLng(viewModel.stopModel.latitude, viewModel.stopModel.longitude)

        binding.edSearchPlace.setText(stop.stopName)
        binding.mapLayout.visibility = View.VISIBLE
        binding.animationLayout.visibility = View.GONE

        mapFragment.getMapAsync { googleMap: GoogleMap? ->
            run {
                mapFragment.getMapAsync { googleMap ->
                    val success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            this@AddStopsActivity,
                            R.raw.mapstyle
                        )
                    )
                    googleMap.uiSettings.isMyLocationButtonEnabled = false
                    googleMap.uiSettings.isZoomControlsEnabled = false
                    googleMap.uiSettings.setAllGesturesEnabled(false)
                    val markerOptions = MarkerOptions().position(latlng)
                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            latlng,
                            15f
                        )
                    )
                    googleMap.addMarker(markerOptions)
                }
                Log.d("AddStopActivity", "Map Ready")
            }
        }
    }

    private fun setUpActionbar() {
        (binding.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.add_stops)
        binding.actionBar.findViewById<View>(R.id.imgBack)
            .setOnClickListener { v: View? -> onBackPressed() }
    }

    private fun manageClicks() {
        binding.edSearchPlace.setOnClickListener {
            val fields: List<Place.Field> = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields
            ).setCountry("HK")
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }
    }

    private fun setObservers() {
        viewModel.errorLiveData.observe(this) {
            Commons.showToast(this@AddStopsActivity, it)
        }

        viewModel.baseResponseLiveData.observe(this) {
            if (it.code.equals("00")) {
                Commons.showToast(this@AddStopsActivity, it.desc)
                onBackPressed()
            } else {
                Commons.showToast(this@AddStopsActivity, it.desc)
            }
        }

        viewModel.stopsResponseLiveData.observe(this) { stopsResponse: StopsResponse ->
            if (stopsResponse.code == "00") {
                Commons.showToast(this@AddStopsActivity, stopsResponse.desc)
                onBackPressed()
            } else {
                Commons.showToast(this@AddStopsActivity, stopsResponse.desc)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        place = Autocomplete.getPlaceFromIntent(data)
                        val intent =
                            Intent(this@AddStopsActivity, DragableMapActivity::class.java)
                        intent.putExtra("latitude", place.latLng.latitude)
                        intent.putExtra("longitude", place.latLng.longitude)
                        startActivityForResult(intent, 21512)
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Commons.showToast(this@AddStopsActivity, status.statusMessage)
                    }
                }
                Activity.RESULT_CANCELED -> {
                    Commons.showToast(
                        this@AddStopsActivity,
                        getString(R.string.str_stopLocationNotFound)
                    )
                }
            }
            return
        } else if (requestCode == 21512) {
            var latitude = data?.getDoubleExtra("latitude", 0.0)
            var longitude = data?.getDoubleExtra("longitude", 0.0)
            val address = data?.getStringExtra("address")

            if (address!!.isEmpty()) {
                binding.edSearchPlace.setText(place.address)
            } else {
                binding.edSearchPlace.setText(place.address)
            }
            binding.mapLayout.visibility = View.VISIBLE
            binding.animationLayout.visibility = View.GONE



            if (latitude != null && longitude != null) {
                viewModel.stopModel.latitude = latitude
                viewModel.stopModel.longitude = longitude
            } else {
                latitude = place.latLng.latitude
                longitude = place.latLng.longitude

                viewModel.stopModel.latitude = latitude
                viewModel.stopModel.longitude = longitude
            }

            mapFragment.getMapAsync { googleMap: GoogleMap? ->
                run {
                    mapFragment.getMapAsync { googleMap ->
                        /*googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                this@AddStopsActivity,
                                R.raw.mapstyle
                            )
                        )*/
                        googleMap.uiSettings.isMyLocationButtonEnabled = false
                        googleMap.uiSettings.isZoomControlsEnabled = false
                        googleMap.uiSettings.setAllGesturesEnabled(false)
                        val markerOptions =
                            MarkerOptions().position(LatLng(latitude, longitude))
                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(latitude, longitude),
                                15f
                            )
                        )
                        googleMap.addMarker(markerOptions)
                    }
                    Log.d("AddStopActivity", "Map Ready")
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}