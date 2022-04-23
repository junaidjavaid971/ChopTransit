package ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import app.com.choptransit.R
import app.com.choptransit.databinding.ActivityViewDriverDetailsBinding
import app.com.choptransit.models.response.DriverData
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.DriverViewModel

class ViewDriverDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityViewDriverDetailsBinding
    lateinit var viewModel: DriverViewModel
    lateinit var driver: DriverData
    var isRequest = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_driver_details)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(DriverViewModel::class.java)

        driver = intent.getSerializableExtra("driver") as DriverData
        isRequest = intent.getBooleanExtra("isRequest", false)

        setUpActionbar()
        setupDriver()
        setObservers()
        manageClicks()

        viewModel.getDriverProfile(driver.id)
    }

    private fun setUpActionbar() {
        (binding.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.str_driverDetails)
        binding.actionBar.findViewById<View>(R.id.imgBack)
            .setOnClickListener { v: View? -> onBackPressed() }
    }

    private fun setupDriver() {
        if (isRequest) {
            binding.layoutRequestControls.visibility = View.VISIBLE
            binding.layoutControls.visibility = View.GONE
        } else {
            binding.layoutControls.visibility = View.VISIBLE
            binding.layoutRequestControls.visibility = View.GONE
        }

        binding.tvDriverName.text = driver.getFirstName() + " " + driver.getLastName()
        binding.tvCompanyName.text = driver.getCompanyName()
        binding.tvDriverContactNumber.text = driver.getPhoneNumber()
        binding.tvDriverEmail.text = driver.getEmail()

        if (driver.busName.isNotEmpty()) {
            var regNo = ""
            if (driver.busRegNo.isNotEmpty()) {
                regNo = driver.busRegNo
            }

            binding.tvAssignedBus.text = driver.busName + " (" + regNo + ")"
        } else {
            binding.tvAssignedBus.text = getString(R.string.str_notAssignedToAnyBus)
        }
    }

    private fun setObservers() {
        viewModel.dialogLiveData.observe(this) {
            if (it) {
                Commons.showProgress(this@ViewDriverDetailsActivity)
            } else {
                Commons.hideProgress()
            }
        }

        viewModel.driversResponseLiveData.observe(this) {
            if (it.code.equals("00")) {
                Commons.showToast(this@ViewDriverDetailsActivity, it.desc)
                onBackPressed()
            } else {
                Commons.showToast(this@ViewDriverDetailsActivity, it.desc)
            }
        }

        viewModel.driverDeletedLiveData.observe(this) {
            if (it.code.equals("00")) {
                Commons.showToast(this@ViewDriverDetailsActivity, "Driver Deleted")
                onBackPressed()
            } else {
                Commons.showToast(this@ViewDriverDetailsActivity, it.desc)
            }
        }

        viewModel.driverProfileLiveData.observe(this) {
            if (it.code.equals("00")) {
                driver = it.data
                setupDriver()
            }
        }
    }

    private fun manageClicks() {
        binding.btnApprove.setOnClickListener {
            Commons.showAlertDialog(
                "Approve Driver",
                "Are you sure you want to approve this driver?",
                this
            ) {
                viewModel.approveDriver(driver.id)
            }
        }

        binding.btnReject.setOnClickListener {
            Commons.showAlertDialog(
                "Approve Driver",
                "Are you sure you want to reject this driver's application?",
                this
            ) {
                viewModel.rejectDriver(driver.id, false)
            }
        }

        binding.btnDeleteDriver.setOnClickListener {
            Commons.showAlertDialog(
                "Delete Driver",
                "Are you sure you want to delete this driver?",
                this
            ) {
                viewModel.deleteDriver(driver.id, true)
            }
        }

        binding.btnAssignDriver.setOnClickListener {
            startActivity(
                Intent(
                    this@ViewDriverDetailsActivity,
                    SelectBusActivity::class.java
                ).putExtra("driverID", driver.id)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getDriverProfile(driver.id)
    }


}