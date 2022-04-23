package ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import app.com.choptransit.R
import app.com.choptransit.databinding.ActivityRideBookBinding
import app.com.choptransit.models.observer.CardObserverModel
import app.com.choptransit.models.response.AssignRouteData
import app.com.choptransit.models.response.PassengerResponse
import app.com.choptransit.models.response.Stop
import app.com.choptransit.utilities.Commons
import app.com.choptransit.utilities.SharePrefData
import app.com.choptransit.viewmodels.AdminViewModel
import app.com.choptransit.viewmodels.PassengerViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import org.json.JSONArray
import org.json.JSONObject
import ui.CardBottomSheetLayout
import ui.ChoosePaymentMethodSheet
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class RideBookActivity : AppCompatActivity(), ChoosePaymentMethodSheet.ItemClickListener,
    CardBottomSheetLayout.ItemClickListener {

    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    lateinit var passengerViewModel: PassengerViewModel
    lateinit var viewModel: AdminViewModel
    var bookingConfirmed = false

    lateinit var route: AssignRouteData
    lateinit var stop: Stop
    lateinit var passenger: PassengerResponse.PassengerData

    lateinit var choosePaymentMethodSheet: ChoosePaymentMethodSheet
    lateinit var cardBottomSheetLayout: CardBottomSheetLayout
    lateinit var binding: ActivityRideBookBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_book)
        viewModel = ViewModelProvider(this)[AdminViewModel::class.java]
        passengerViewModel = ViewModelProvider(this)[PassengerViewModel::class.java]

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        route = intent.getSerializableExtra("route") as AssignRouteData
        stop = intent.getSerializableExtra("stop") as Stop

        passenger = SharePrefData.getInstance().getPassengerPref(this@RideBookActivity, "passenger")

        binding.tvBusNumber.text = route.bus.busRegNo
        binding.tvPriceTicket.text = "$" + route.route.fare
        binding.tvStopName.text = stop.stopName
        binding.tvVacancyNo.text = route.bus.vacancy

        getCurrentLocation()
        setObservers()
        manageClicks()
    }

    private fun setObservers() {
        viewModel.baseResponseLiveData.observe(this) {
            if (it.code.equals("00")) {
                binding.tvBookingConfirmed.visibility = View.VISIBLE
                binding.btnBooking.visibility = View.GONE
                binding.btnConfirm.visibility = View.GONE

                binding.tvBookingConfirmed.text = it.desc
                bookingConfirmed = true

                choosePaymentMethodSheet.dismiss()
            } else {
                Commons.showToast(this@RideBookActivity, it.desc)
            }
        }

        viewModel.dialogLiveData.observe(this) {
            if (it) {
                Commons.showProgress(this@RideBookActivity)
            } else {
                Commons.hideProgress()
            }
        }

        passengerViewModel.dialogLiveData.observe(this) {
            if (it) {
                Commons.showProgress(this@RideBookActivity)
            } else {
                Commons.hideProgress()
            }
        }

        passengerViewModel.transactionLiveData.observe(this) {
            if (it.code.equals("00")) {
                viewModel.bookRide(passenger.id, route.id, route.bus.driver.id, stop.stopName)
            } else {
                Commons.showToast(this@RideBookActivity, it.desc)
            }
        }

        passengerViewModel.passengerResponseLiveData.observe(this) {
            passenger = it.data

            SharePrefData.getInstance().deletePrefData(this@RideBookActivity, "passenger")
            SharePrefData.getInstance()
                .setPassengerData(this@RideBookActivity, "passenger", passenger)

            passengerViewModel.performTransaction(
                passenger.customerID,
                route.route.fare,
                passenger.id
            )
        }

        passengerViewModel.saveCardLiveData.observe(this) {
            if (it.code.equals("00")) {
                Commons.showToast(this@RideBookActivity, it.desc)
                passengerViewModel.getPassengerInfo(passenger.id, 6)

                cardBottomSheetLayout.dismiss()
            } else {
                Commons.showToast(this@RideBookActivity, it.desc)
            }
        }
    }

    private fun manageClicks() {
        binding.btnBooking.setOnClickListener {
            binding.btnBooking.visibility = View.GONE
            binding.btnConfirm.visibility = View.VISIBLE
        }

        binding.btnConfirm.setOnClickListener {
            choosePaymentMethodSheet = ChoosePaymentMethodSheet.newInstance()
            choosePaymentMethodSheet.show(
                supportFragmentManager,
                CardBottomSheetLayout.TAG
            )
        }

        binding.ivBack.setOnClickListener {
            if (bookingConfirmed) {
                startActivity(
                    Intent(this@RideBookActivity, MainActivity::class.java)
                        .putExtra(
                            "passenger",
                            SharePrefData.getInstance()
                                .getPassengerPref(this@RideBookActivity, "passenger")
                        )
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                finish()
            } else {
                onBackPressed()
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

                if (duration.isEmpty()) {
                    binding.tvETANo.text = "N/A"
                } else {
                    binding.tvETANo.text = duration
                }
            } catch (e: Exception) {
                binding.tvETANo.text = "N/A"
                return
            }
        }
    }

    private fun requestDirection(requestedUrl: String): String {
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

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this@RideBookActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@RideBookActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val task: Task<Location> = fusedLocationProviderClient?.getLastLocation() as Task<Location>
        task.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val destination = LatLng(location.latitude, location.longitude)
                val origin = LatLng(stop.latitude.toDouble(), stop.longitude.toDouble())
                TaskDirectionRequest().execute(getRequestedUrl(origin, destination))
            }
        }
    }

    override fun onBackPressed() {
        if (bookingConfirmed) {
            startActivity(
                Intent(this@RideBookActivity, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun onPaySelected(isCard: Boolean) {
        if (isCard) {
            if (passenger.customerID.isEmpty() || passenger.cardID.isEmpty()) {

                cardBottomSheetLayout = CardBottomSheetLayout.newInstance()
                cardBottomSheetLayout.show(
                    supportFragmentManager,
                    CardBottomSheetLayout.TAG
                )
            } else {
                passengerViewModel.performTransaction(
                    passenger.customerID,
                    route.route.fare,
                    passenger.id
                )
            }
        } else {
            Commons.showToast(
                this@RideBookActivity,
                "Alipay is not available in test mode. Please use live keys!"
            )
        }
    }

    override fun onCancelClicked() {
        cardBottomSheetLayout.dismiss()
    }

    override fun onAddCardClicked(cardObserverModel: CardObserverModel?) {
        passengerViewModel.saveCardDetails(cardObserverModel, passenger.id, passenger.email)
    }
}