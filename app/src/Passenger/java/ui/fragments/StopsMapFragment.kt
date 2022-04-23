package ui.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import app.com.choptransit.R
import app.com.choptransit.models.response.AssignRouteData
import app.com.choptransit.utilities.Commons
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class StopsMapFragment : Fragment {
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var mapFragment: SupportMapFragment? = null
    var mMap: GoogleMap? = null
    var mPolyline: Polyline? = null
    var routeData: AssignRouteData? = null

    constructor() {}
    constructor(route: AssignRouteData) {
        routeData = route
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        if (mapFragment != null) {
            mapFragment!!.getMapAsync(callback)
        }
    }

    private val callback = OnMapReadyCallback { googleMap: GoogleMap ->
        mMap = googleMap
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireActivity(),
                R.raw.mapstyle
            )
        )
        for (stop in routeData!!.route.stops) {
            val latLng = LatLng(stop.latitude.toDouble(), stop.longitude.toDouble())
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            googleMap.uiSettings.isZoomControlsEnabled = true
            val markerOptions = MarkerOptions().position(latLng)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            googleMap.addMarker(markerOptions)
        }
        drawPolyLines()
    }

    private fun drawPolyLines() {
        val list: ArrayList<LatLng?> = ArrayList()

        if (routeData!!.route.stops.size == 2) {
            val stopArrayList = routeData!!.route.stops
            val origin =
                LatLng(stopArrayList[0].latitude.toDouble(), stopArrayList[0].longitude.toDouble())
            val destination = LatLng(
                stopArrayList[stopArrayList.size - 1].latitude.toDouble(),
                stopArrayList[stopArrayList.size - 1].longitude.toDouble()
            )
            TaskDirectionRequest().execute(
                getRequestedUrl(
                    origin, destination
                )
            )
        }
        if (routeData!!.route.stops.size > 2) {
            val stopArrayList = routeData!!.route.stops
            for (i in 0..stopArrayList.size) {
                if (i == stopArrayList.size - 1) {
                    return
                }
                val origin =
                    LatLng(
                        stopArrayList[i].latitude.toDouble(),
                        stopArrayList[i].longitude.toDouble()
                    )
                val destination = LatLng(
                    stopArrayList[i + 1].latitude.toDouble(),
                    stopArrayList[i + 1].longitude.toDouble()
                )
                TaskDirectionRequest().execute(
                    getRequestedUrl(
                        origin, destination
                    )
                )
            }
        }
    }


    private fun getRequestedUrl(origin: LatLng, destination: LatLng): String {
        val strOrigin = "origin=" + origin.latitude + "," + origin.longitude
        val strDestination = "destination=" + destination.latitude + "," + destination.longitude
        val sensor = "sensor=false"
        val mode = "mode=driving"
        val param = "$strOrigin&$strDestination&$sensor&$mode"
        val output = "json"
        val APIKEY = "&key=" + getString(R.string.str_APIKEY)
        return "https://maps.googleapis.com/maps/api/directions/$output?$param$APIKEY"
    }

    inner class TaskDirectionRequest : AsyncTask<String?, Void?, String>() {
        override fun doInBackground(vararg params: String?): String? {
            var responseString = ""
            try {
                responseString = requestDirection(params[0].toString()).toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return responseString
        }

        override fun onPostExecute(responseString: String) {
            super.onPostExecute(responseString)
            try {
                val jsonObject = JSONObject(responseString)
                val routes: JSONArray = jsonObject.getJSONArray("routes")
                val `object` = routes.getJSONObject(0)
                val legs = `object`.getJSONArray("legs")
                val legsObjects = legs.getJSONObject(0)
                val time = legsObjects.getJSONObject("duration")
                val duration = time.getString("text")

            } catch (e: Exception) {
                print(e.localizedMessage)
                return
            }
            val parseResult = TaskParseDirection()
            parseResult.execute(responseString)
        }
    }

    inner class TaskParseDirection :
        AsyncTask<String, Void, List<List<HashMap<String, String>>>>() {
        override fun doInBackground(vararg params: String?): List<List<HashMap<String, String>>>? {
            var routes: List<List<HashMap<String, String>>>? = null
            var jsonObject: JSONObject? = null

            try {
                jsonObject = JSONObject(params[0])
                val parser = DirectionsJSONParser()
                routes = parser.parse(jsonObject)
            } catch (e: JSONException) {
                print(e.localizedMessage)
            }
            return routes
        }

        override fun onPostExecute(lists: List<List<HashMap<String, String>>>?) {
            super.onPostExecute(lists)
            val points: ArrayList<LatLng>? = ArrayList()
            var polylineOptions: PolylineOptions? = null
            for (path in lists!!) {
                polylineOptions = PolylineOptions()
                for (point in path) {
                    val lat = point["lat"]!!.toDouble()
                    val lon = point["lng"]!!.toDouble()
                    points?.add(LatLng(lat, lon))
                }
                points?.let { polylineOptions.addAll(it) }
                polylineOptions.width(10f)
                polylineOptions.color(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.primaryColor
                    )
                )
                polylineOptions.geodesic(true)
            }
            if (polylineOptions != null) {
                mPolyline = mMap?.addPolyline(polylineOptions)
            } else {
                Toast.makeText(requireActivity(), "Direction not found", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun requestDirection(requestedUrl: String): String? {
        var responseString = ""
        var inputStream: InputStream? = null
        var httpURLConnection: HttpURLConnection? = null
        try {
            val url = URL(requestedUrl)
            httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.connect()
            inputStream = httpURLConnection.getInputStream()
            val reader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(reader)
            val stringBuffer = StringBuffer()
            var line: String? = ""
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuffer.append(line)
            }
            responseString = stringBuffer.toString()
            bufferedReader.close()
            reader.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        httpURLConnection?.disconnect()
        return responseString
    }

}