package ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import app.com.choptransit.R
import app.com.choptransit.databinding.ActivityAddBusBinding
import app.com.choptransit.models.response.BusResponse
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.AdminViewModel

class AddBusActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddBusBinding
    lateinit var viewModel: AdminViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_bus)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AdminViewModel::class.java)
        binding.addBusViewModel = viewModel

        setUpActionbar()
        setObservers()

        if (intent.hasExtra("bus")) {
            val bus = intent.getSerializableExtra("bus") as BusResponse.BusData
            setupBusForm(bus)
        }
    }

    private fun setupBusForm(bus: BusResponse.BusData) {
        binding.btnAddBus.text = getString(R.string.str_updateBus)
        (binding.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.str_updateBus)
        viewModel.busModel.id = bus.id
        viewModel.busModel.busName = bus.busName
        viewModel.busModel.busRegistrationNumber = bus.busRegNo
        viewModel.busModel.busColor = bus.busColor
        viewModel.busModel.busType = bus.busType
        viewModel.busModel.vacancy = bus.vacancy
        viewModel.busModel.updateBus = true
    }

    private fun setUpActionbar() {
        (binding.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.str_addBus)
        binding.actionBar.findViewById<View>(R.id.imgBack)
            .setOnClickListener { v: View? -> onBackPressed() }
    }

    private fun setObservers() {
        viewModel.errorLiveData.observe(this) {
            Commons.showToast(this@AddBusActivity, it)
        }

        viewModel.dialogLiveData?.observe(this@AddBusActivity) {
            if (it) {
                Commons.showProgress(this@AddBusActivity)
            } else {
                Commons.hideProgress()
            }
        }

        viewModel.baseResponseLiveData.observe(this) {
            if (it.code == "00") {
                Commons.showToast(this@AddBusActivity, it.desc)
                onBackPressed()
            } else {
                Commons.showToast(this@AddBusActivity, it.desc)
            }
        }
    }
}