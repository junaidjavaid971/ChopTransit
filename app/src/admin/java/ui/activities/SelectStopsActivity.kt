package ui.activities

import adapters.SelectStopsAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.com.choptransit.R
import app.com.choptransit.databinding.ActivitySelectStopsBinding
import app.com.choptransit.models.response.Stop
import app.com.choptransit.models.response.StopsResponse
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.AdminViewModel

class SelectStopsActivity : AppCompatActivity(), SelectStopsAdapter.StopSelectedCallback {
    var binding: ActivitySelectStopsBinding? = null
    var viewModel: AdminViewModel? = null
    var stopsList = ArrayList<Stop>()
    var receivedStops = ArrayList<Stop>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_stops)
        setContentView(binding?.root)
        viewModel = ViewModelProvider(this)[AdminViewModel::class.java]
        binding?.selectRouteViewModel = viewModel

        if (intent.hasExtra("stops")) {
            receivedStops = intent.getSerializableExtra("stops") as ArrayList<Stop>
            stopsList.addAll(receivedStops)
        }
        getStops()
        setUpActionbar()
        setObservers()
        manageClicks()
    }

    private fun setUpActionbar() {
        (binding!!.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.str_selectStops)
        binding!!.actionBar.findViewById<View>(R.id.imgBack)
            .setOnClickListener { v: View? -> onBackPressed() }
    }

    private fun manageClicks() {
        binding?.btnDone?.setOnClickListener {
            val intent = Intent()
            intent.putExtra("stopsList", stopsList)
            setResult(1121, intent)
            finish()
        }
    }

    private fun getStops() {
        viewModel!!.getStops()
    }

    private fun setObservers() {
        viewModel!!.stopsResponseLiveData.observe(this) { stopsResponse: StopsResponse ->
            if (stopsResponse.code == "00") {
                val stopArrayList = stopsResponse.data.stopsList
                initStopsRecyclerview(stopArrayList)
            } else {
                Commons.showToast(this@SelectStopsActivity, stopsResponse.desc)
            }
        }

        viewModel?.dialogLiveData?.observe(this) {
            if (it) {
                Commons.showProgress(this)
            } else {
                Commons.hideProgress()
            }
        }
    }

    private fun initStopsRecyclerview(stopArrayList: ArrayList<Stop>) {
        val adapter = SelectStopsAdapter(stopArrayList, receivedStops, this, this)
        binding?.rvStops?.setHasFixedSize(true)
        binding?.rvStops?.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(
            binding?.rvStops?.context,
            DividerItemDecoration.VERTICAL
        )
        binding?.rvStops?.addItemDecoration(dividerItemDecoration)
        binding?.rvStops?.adapter = adapter
    }


    override fun onStopSelected(stop: Stop?, isChecked: Boolean) {
        if (isChecked) {
            if (stop != null) {
                stopsList.add(stop)
            }
        } else {
            for (checkedStop in stopsList) {
                if (checkedStop.id == stop!!.id) {
                    stopsList.remove(checkedStop)
                    break
                }
            }
        }
    }


}