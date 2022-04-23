package ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.com.choptransit.R
import app.com.choptransit.adapters.StopTimelineAdapter
import app.com.choptransit.databinding.ActivityViewBusDetailsBinding
import app.com.choptransit.models.response.BusResponse
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.AdminViewModel

class ViewBusDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityViewBusDetailsBinding
    lateinit var bus: BusResponse.BusData
    lateinit var viewModel: AdminViewModel
    var busID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_bus_details)
        setContentView(binding.root)

        busID = intent.getStringExtra("busID").toString()
        viewModel = ViewModelProvider(this)[AdminViewModel::class.java]
        setUpActionbar()

        setupObservers()
        viewModel.getBusByID(busID)
    }

    private fun setupObservers() {
        viewModel.busResponseLiveData.observe(this) {
            if (it.code.equals("00")) {
                bus = it.data.busesList[0]
                setupBus()
            }
        }
        viewModel.dialogLiveData.observe(this) {
            if (it) {
                Commons.showProgress(this@ViewBusDetailsActivity)
            } else {
                Commons.hideProgress()
            }
        }
        viewModel.baseResponseLiveData.observe(this) {
            if (it.code.equals("00")) {
                Commons.showToast(this@ViewBusDetailsActivity, it.desc)
                onBackPressed()
            } else {
                Commons.showToast(this@ViewBusDetailsActivity, it.desc)
            }
        }
    }


    private fun manageClicks() {
        binding.btnEdit.setOnClickListener {
            startActivity(
                Intent(
                    this@ViewBusDetailsActivity,
                    AddBusActivity::class.java
                ).putExtra("bus", bus)
            )
        }

        binding.btnDelete.setOnClickListener {
            Commons.showAlertDialog(
                "Delete Bus",
                "Are you sure you want to delete this bus? ",
                this
            ) {
                viewModel.deleteBus(bus.id)
            }
        }
    }

    private fun setUpActionbar() {
        (binding.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.str_BusDetails)
        binding.actionBar.findViewById<View>(R.id.imgBack)
            .setOnClickListener { v: View -> onBackPressed() }
    }

    private fun setupBus() {
        binding.tvBusName.text = bus.busName
        binding.tvRegistrationNumber.text = bus.busRegNo
        binding.tvBusType.text = bus.busType
        binding.tvBusColor.text = bus.busColor

        if (bus.driver == null) {
            binding.tvAssignedDriver.text = getString(R.string.str_notAssignedToAnyDriver)
        } else {
            binding.tvAssignedDriver.text = bus.driver.firstName + " " + bus.driver.lastName + " (" + bus.driver.phoneNumber + ")"
        }
        manageClicks()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getBusByID(busID)
    }
}