package app.com.choptransit.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.com.choptransit.R
import app.com.choptransit.adapters.ConfirmedPassengersAdapter
import app.com.choptransit.databinding.ActivityPickPassengersBinding
import app.com.choptransit.models.response.BookingResponseData
import app.com.choptransit.models.response.DriverData
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.DriverViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions


class PickPassengersActivity : BaseActivity(),
    ConfirmedPassengersAdapter.BookingControlsCallback {
    lateinit var barcodeLauncher: ActivityResultLauncher<ScanOptions>
    lateinit var binding: ActivityPickPassengersBinding
    lateinit var viewModel: DriverViewModel
    lateinit var driver: DriverData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_pick_passengers)
        viewModel = ViewModelProvider(this)[DriverViewModel::class.java]

        driver = intent.getSerializableExtra("driver") as DriverData
        setUpActionbar()
        setObservers()
        viewModel.getActiveOrConfirmedRides(driver.id)
    }

    private fun setUpActionbar() {
        (binding.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.str_RideRequests)
        binding.actionBar.findViewById<View>(R.id.imgBack)
            .setOnClickListener { v: View? -> onBackPressed() }
    }

    private fun setObservers() {
        viewModel.bookingResponseLiveData.observe(this) {
            if (it.code.equals("00")) {
                if (it.data.bookingsList.isEmpty()) {
                    binding.animationLayout.visibility = View.VISIBLE
                    binding.rvPassengers.visibility = View.GONE
                } else {
                    binding.animationLayout.visibility = View.GONE
                    binding.rvPassengers.visibility = View.VISIBLE

                    val adapter = ConfirmedPassengersAdapter(it.data.bookingsList, this, this)
                    binding.rvPassengers.setHasFixedSize(true)
                    binding.rvPassengers.layoutManager = LinearLayoutManager(this)
                    val dividerItemDecoration = DividerItemDecoration(
                        binding.rvPassengers.context,
                        DividerItemDecoration.VERTICAL
                    )
                    binding.rvPassengers.addItemDecoration(dividerItemDecoration)
                    binding.rvPassengers.adapter = adapter
                }
            }
        }

        viewModel.baseResponseLiveData.observe(this) {
            if (it.code == "00") {
                Commons.showToast(this@PickPassengersActivity, it.desc)
                viewModel.getBookingRequests(driver.id)
            } else {
                Commons.showToast(this@PickPassengersActivity, it.desc)
            }
        }
        viewModel.pickPassengerLiveData.observe(this) {
            if (it.code == "00") {
                Commons.showToast(this@PickPassengersActivity, it.desc)
                viewModel.getBookingRequests(driver.id)
            } else {
                Commons.showToast(this@PickPassengersActivity, it.desc)
            }
        }
        viewModel.endRideLiveData.observe(this) {
            if (it.code == "00") {
                Commons.showToast(this@PickPassengersActivity, it.desc)
                viewModel.getBookingRequests(driver.id)
            } else {
                Commons.showToast(this@PickPassengersActivity, it.desc)
            }
        }
        viewModel.missPassengerLiveData.observe(this) {
            if (it.code == "00") {
                Commons.showToast(this@PickPassengersActivity, it.desc)
                viewModel.getBookingRequests(driver.id)
            } else {
                Commons.showToast(this@PickPassengersActivity, it.desc)
            }
        }

        viewModel.dialogLiveData.observe(this@PickPassengersActivity) { showDialog: Boolean ->
            if (showDialog) {
                Commons.showProgress(this@PickPassengersActivity)
            } else {
                Commons.hideProgress()
            }
        }

        barcodeLauncher = registerForActivityResult(
            ScanContract()
        ) { result: ScanIntentResult ->
            if (result.contents == null) {
                Toast.makeText(this@PickPassengersActivity, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    this@PickPassengersActivity,
                    "Scanned: " + result.contents,
                    Toast.LENGTH_LONG
                ).show()

                val bookingID = result.contents

                viewModel.pickPassenger(bookingID)
            }
        }
    }

    override fun onPickPassengerClicked(booking: BookingResponseData) {
        val options = ScanOptions()
        options.setPrompt("Scan passenger's QR Code.")
        options.setOrientationLocked(true)
        barcodeLauncher.launch(options)
    }

    override fun onMissPassengerClicked(booking: BookingResponseData) {
        viewModel.missPassenger(booking.id)
    }

    override fun onEndRideClicked(booking: BookingResponseData) {
        viewModel.finishRide(booking.id)
    }

}