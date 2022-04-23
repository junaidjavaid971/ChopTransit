package ui.activities

import adapters.DriversAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.com.choptransit.R
import app.com.choptransit.databinding.ActivityDriversBinding
import app.com.choptransit.models.response.DriverData
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.AdminViewModel

class DriversActivity : AppCompatActivity() {

    lateinit var binding: ActivityDriversBinding
    lateinit var viewModel: AdminViewModel
    lateinit var adapter: DriversAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_drivers)
        viewModel = ViewModelProvider(this)[AdminViewModel::class.java]

        setObservers()
        manageClicks()
        setUpActionbar()
        viewModel.getDrivers()
    }

    private fun setUpActionbar() {
        (binding.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.str_Drivers)
        binding.actionBar.findViewById<View>(R.id.imgBack)
            .setOnClickListener { v: View? -> onBackPressed() }
    }

    private fun manageClicks() {
        binding.txtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                adapter.filter(s.toString())
            }
        })

        binding.imgCancel.setOnClickListener {
            binding.txtSearch.clearFocus()
            binding.txtSearch.text = null
            adapter.filter("")
        }
    }

    private fun setObservers() {
        viewModel.dialogLiveData?.observe(this@DriversActivity) {
            if (it) {
                Commons.showProgress(this@DriversActivity)
            } else {
                Commons.hideProgress()
            }
        }

        viewModel.driversResponseLiveData.observe(this@DriversActivity) {
            if (it.code == "00") {
                initDriverRequestsRecyclerview(it.data)
            }
        }
    }

    private fun initDriverRequestsRecyclerview(driversArrayList: ArrayList<DriverData>) {
        adapter = DriversAdapter(driversArrayList, this, isRequest = false)
        binding.rvDriver.setHasFixedSize(true)
        binding.rvDriver.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(
            binding.rvDriver.context,
            DividerItemDecoration.VERTICAL
        )
        binding.rvDriver.addItemDecoration(dividerItemDecoration)
        binding.rvDriver.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.getDrivers()
    }
}