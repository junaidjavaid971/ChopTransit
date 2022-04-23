package ui.activities

import adapters.BusAdapter
import adapters.SelectBusAdapter
import android.content.Intent
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
import app.com.choptransit.databinding.ActivitySelectBusBinding
import app.com.choptransit.models.response.BusResponse
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.AdminViewModel

class SelectBusActivity : AppCompatActivity(), SelectBusAdapter.BusSelectedCallback {

    lateinit var binding: ActivitySelectBusBinding
    lateinit var viewModel: AdminViewModel

    lateinit var adapter: SelectBusAdapter
    var driverID = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_bus)
        viewModel = ViewModelProvider(this)[AdminViewModel::class.java]


        viewModel = ViewModelProvider(this).get(AdminViewModel::class.java)


        if (intent.hasExtra("driverID")) {
            driverID = intent.getStringExtra("driverID").toString()
        }

        setUpActionbar()
        manageClicks()
        setObservers()

        viewModel.getAssignedBuses()
    }

    private fun setUpActionbar() {
        (binding.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.str_buses)
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
        viewModel.dialogLiveData.observe(this) {
            if (it) {
                Commons.showProgress(this@SelectBusActivity)
            } else {
                Commons.hideProgress()
            }
        }

        viewModel.busResponseLiveData.observe(this) {
            if (it.code == "00") {
                initRoutesRecyclerview(it.data.busesList)
            }
        }

        viewModel.baseResponseLiveData.observe(this) {
            if (it.code.equals("00")) {
                Commons.showToast(this@SelectBusActivity, it.desc)
                onBackPressed()
            } else {
                Commons.showToast(this@SelectBusActivity, it.desc)
            }
        }
    }

    private fun initRoutesRecyclerview(busArrayList: ArrayList<BusResponse.BusData>) {
        adapter = SelectBusAdapter(busArrayList, this, this)
        binding.rvBuses.setHasFixedSize(true)
        binding.rvBuses.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(
            binding.rvBuses.context,
            DividerItemDecoration.VERTICAL
        )
        binding.rvBuses.addItemDecoration(dividerItemDecoration)
        binding.rvBuses.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.getBuses()
    }

    override fun onBusSelected(bus: BusResponse.BusData) {
        if (!driverID.isEmpty()) {
            viewModel.assignBusToDriver(bus.id, driverID)
        } else {
            val intent = Intent()
            intent.putExtra("bus", bus)
            setResult(2321, intent)
            finish()
        }
    }
}