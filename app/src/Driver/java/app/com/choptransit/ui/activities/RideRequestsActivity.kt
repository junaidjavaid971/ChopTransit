package app.com.choptransit.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.com.choptransit.R
import app.com.choptransit.adapters.BookingRequestsAdapter
import app.com.choptransit.databinding.ActivityRideRequestsBinding
import app.com.choptransit.models.response.BookingResponseData
import app.com.choptransit.models.response.DriverData
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.AdminViewModel
import app.com.choptransit.viewmodels.DriverViewModel

class RideRequestsActivity : BaseActivity(), BookingRequestsAdapter.BookingControlsCallback {
    lateinit var binding: ActivityRideRequestsBinding
    lateinit var viewModel: DriverViewModel
    lateinit var driver: DriverData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_requests)
        viewModel = ViewModelProvider(this)[DriverViewModel::class.java]

        driver = intent.getSerializableExtra("driver") as DriverData
        setUpActionbar()
        setObservers()
        viewModel.getBookingRequests(driver.id)
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
                    binding.tvNoResultFound.visibility = View.VISIBLE
                    binding.rvRideRequests.visibility = View.GONE
                } else {
                    binding.tvNoResultFound.visibility = View.GONE
                    binding.rvRideRequests.visibility = View.VISIBLE

                    val adapter = BookingRequestsAdapter(it.data.bookingsList, this, this)
                    binding.rvRideRequests.setHasFixedSize(true)
                    binding.rvRideRequests.layoutManager = LinearLayoutManager(this)
                    val dividerItemDecoration = DividerItemDecoration(
                        binding.rvRideRequests.context,
                        DividerItemDecoration.VERTICAL
                    )
                    binding.rvRideRequests.addItemDecoration(dividerItemDecoration)
                    binding.rvRideRequests.adapter = adapter
                }
            }
        }

        viewModel.baseResponseLiveData.observe(this) {
            if (it.code == "00") {
                Commons.showToast(this@RideRequestsActivity, it.desc)
                viewModel.getBookingRequests(driver.id)
            } else {
                Commons.showToast(this@RideRequestsActivity, it.desc)
                viewModel.getBookingRequests(driver.id)
            }
        }

        viewModel.dialogLiveData.observe(this@RideRequestsActivity) { showDialog: Boolean ->
            if (showDialog) {
                Commons.showProgress(this@RideRequestsActivity)
            } else {
                Commons.hideProgress()
            }
        }
    }

    override fun onApproveClicked(booking: BookingResponseData) {
        viewModel.approveBooking(booking.id)
    }

    override fun onRejectClicked(booking: BookingResponseData) {
        viewModel.rejectBooking(booking.id)
    }

}