package app.com.choptransit.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import app.com.choptransit.R
import app.com.choptransit.adapters.RideHistoryAdapter
import app.com.choptransit.databinding.ActivityRideHistoryBinding
import app.com.choptransit.models.response.DriverData
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.DriverViewModel

class DriverRideHistoryActivity : BaseActivity() {

    lateinit var binding: ActivityRideHistoryBinding
    lateinit var viewModel: DriverViewModel
    lateinit var driver: DriverData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ride_history)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_history)
        viewModel = ViewModelProvider(this)[DriverViewModel::class.java]


        driver = intent.getSerializableExtra("driver") as DriverData

        setUpActionbar()
        setObservers()

        viewModel.getDriverFinishedRides(driver.id)
    }

    private fun setUpActionbar() {
        (binding.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.rides_history)
        binding.actionBar.findViewById<View>(R.id.imgBack)
            .setOnClickListener { v: View? -> onBackPressed() }
    }

    private fun setObservers() {
        viewModel.bookingResponseLiveData.observe(this@DriverRideHistoryActivity) {
            if (it.code.equals("00")) {
                if (it.data.bookingsList.isEmpty()) {
                    binding.animation.visibility = View.VISIBLE
                    binding.rvPreviousRides.visibility = View.GONE
                } else {
                    binding.animation.visibility = View.GONE
                    binding.rvPreviousRides.visibility = View.VISIBLE

                    val adapter =
                        RideHistoryAdapter(it.data.bookingsList, this@DriverRideHistoryActivity)
                    binding.rvPreviousRides.setHasFixedSize(true)
                    binding.rvPreviousRides.layoutManager =
                        LinearLayoutManager(this@DriverRideHistoryActivity)
                    binding.rvPreviousRides.adapter = adapter
                }
            }
        }
        viewModel.baseResponseLiveData.observe(this@DriverRideHistoryActivity) {
            if (it.code == "00") {
                Commons.showToast(this@DriverRideHistoryActivity, it.desc)
            } else {
                Commons.showToast(this@DriverRideHistoryActivity, it.desc)
            }
        }

        viewModel.dialogLiveData.observe(this) { showDialog: Boolean ->
            if (showDialog) {
                Commons.showProgress(this)
            } else {
                Commons.hideProgress()
            }
        }
    }

}