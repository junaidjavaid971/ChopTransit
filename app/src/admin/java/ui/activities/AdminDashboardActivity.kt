package ui.activities

import adapters.DriversAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import app.com.choptransit.R
import app.com.choptransit.databinding.ActivityAdminDashboardBinding
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.AdminViewModel

class AdminDashboardActivity : AppCompatActivity() {

    lateinit var binding: ActivityAdminDashboardBinding
    lateinit var viewModel: AdminViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_dashboard)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AdminViewModel::class.java]

        binding.tvDate.text = Commons.getFormattedDate()

        viewModel.getCounts()
        setObservers()
        manageClicks()
    }

    private fun setObservers() {
        viewModel.baseResponseLiveData.observe(this) {
            if (it.desc.isNotEmpty()) {
                val counts = it.desc

                val countlist = counts.split("|")
                binding.tvBookings.text = countlist[0]
                binding.tvAssignedRoutes.text = countlist[1]
                binding.tvDrivers.text = countlist[2]
                binding.tvPassengers.text = countlist[3]
                binding.tvCompanies.text = countlist[4]
            }
        }
    }


    private fun manageClicks() {
        binding.layoutAssignedRoutes.setOnClickListener {
            startActivity(Intent(this@AdminDashboardActivity, AssignedRoutesActivity::class.java))
        }
        binding.layoutRoutes.setOnClickListener {
            startActivity(Intent(this@AdminDashboardActivity, RoutesActivity::class.java))
        }
        binding.layoutStops.setOnClickListener {
            startActivity(Intent(this@AdminDashboardActivity, StopsActivity::class.java))
        }
        binding.layoutDrivers.setOnClickListener {
            startActivity(Intent(this@AdminDashboardActivity, DriversActivity::class.java))
        }
        binding.layoutDriverRequests.setOnClickListener {
            startActivity(Intent(this@AdminDashboardActivity, DriverRequestsActivity::class.java))
        }
        binding.layoutCompanies.setOnClickListener {
            startActivity(Intent(this@AdminDashboardActivity, CompaniesActivity::class.java))
        }
        binding.layoutPassengers.setOnClickListener {
        }
        binding.layoutBuses.setOnClickListener {
            startActivity(Intent(this@AdminDashboardActivity, BusesActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCounts()
    }
}