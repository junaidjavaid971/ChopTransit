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
import app.com.choptransit.databinding.ActivityBookingQrCodeBinding
import app.com.choptransit.databinding.ActivityRideBookBinding
import app.com.choptransit.models.response.AssignRouteData
import app.com.choptransit.models.response.BookingResponseData
import app.com.choptransit.models.response.Stop
import app.com.choptransit.utilities.Commons
import app.com.choptransit.utilities.SharePrefData
import app.com.choptransit.viewmodels.AdminViewModel
import com.bumptech.glide.Glide
import com.google.android.gms.common.internal.service.Common
import com.google.android.gms.dynamite.DynamiteModule.load
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.System.load
import java.net.HttpURLConnection
import java.net.URL

class BookingQRCodeActivity : AppCompatActivity() {
    lateinit var binding: ActivityBookingQrCodeBinding

    lateinit var viewModel: AdminViewModel
    lateinit var route: AssignRouteData
    lateinit var booking: BookingResponseData

    var bookingConfirmed = false
    var fusedLocationProviderClient: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_booking_qr_code)
        viewModel = ViewModelProvider(this)[AdminViewModel::class.java]

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        route = intent.getSerializableExtra("route") as AssignRouteData
        booking = intent.getSerializableExtra("booking") as BookingResponseData

        binding.tvBusNumber.text = route.bus.busRegNo
        binding.tvPriceTicket.text = "$ " + route.route.fare
        binding.tvStopName.text = booking.stopName

        setObservers()
        manageClicks()
    }

    private fun setObservers() {
        Glide.with(this)
            .load("http://chopchop.curlbrackets.com/qrCodes/booking_" + booking.id.toString() + "_qr_code.png")
            .centerCrop()
            .placeholder(R.drawable.driver)
            .into(binding.ivQRCode);

        viewModel.dialogLiveData.observe(this) {
            if (it) {
                Commons.showProgress(this@BookingQRCodeActivity)
            } else {
                Commons.hideProgress()
            }
        }
    }

    private fun manageClicks() {
        binding.btnRideDetails.setOnClickListener {
            startActivity(
                Intent(
                    this@BookingQRCodeActivity,
                    ShowBookingDetailsActivity::class.java
                ).putExtra("route", route).putExtra("active", true)
            )
        }

        binding.ivBack.setOnClickListener {
            if (bookingConfirmed) {
                startActivity(
                    Intent(this@BookingQRCodeActivity, MainActivity::class.java)
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

    override fun onBackPressed() {
        if (bookingConfirmed) {
            startActivity(
                Intent(this@BookingQRCodeActivity, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            finish()
        } else {
            super.onBackPressed()
        }
    }
}