package ui.activities

import adapters.StopsAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.com.choptransit.R
import app.com.choptransit.databinding.ActivityStopsBinding
import app.com.choptransit.models.response.Stop
import app.com.choptransit.models.response.StopsResponse
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.AdminViewModel
import java.util.*
import kotlin.collections.ArrayList


class StopsActivity : AppCompatActivity(), StopsAdapter.StopButtonCallback {

    lateinit var binding: ActivityStopsBinding
    lateinit var viewModel: AdminViewModel
    var stopArrayList = ArrayList<Stop>()
    lateinit var adapter: StopsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_stops)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AdminViewModel::class.java)
        binding.stopsViewModel = viewModel

        setUpActionbar()
        setObservers()
        manageClicks()

        viewModel.getStops()
    }

    private fun setUpActionbar() {
        (binding.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.stops)
        binding.actionBar.findViewById<View>(R.id.imgBack)
            .setOnClickListener { v: View? -> onBackPressed() }
    }

    private fun setObservers() {
        viewModel.stopsResponseLiveData.observe(this) { stopsResponse: StopsResponse ->
            if (stopsResponse.code == "00") {
                stopArrayList = stopsResponse.data.stopsList
                initStopsRecyclerview(stopArrayList)
            } else {
                Commons.showToast(this@StopsActivity, stopsResponse.desc)
            }
        }

        viewModel.dialogLiveData?.observe(this) {
            if (it) {
                Commons.showProgress(this)
            } else {
                Commons.hideProgress()
            }
        }

        viewModel.baseResponseLiveData.observe(this) {
            if (it.code.equals("00")) {
                Commons.showToast(this@StopsActivity, it.desc)
                viewModel.getStops()
            } else {
                Commons.showToast(this@StopsActivity, it.desc)
            }
        }

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

        binding.btnAddStop.setOnClickListener {
            startActivity(Intent(this@StopsActivity, AddStopsActivity::class.java))
        }
    }


    private fun initStopsRecyclerview(stopArrayList: ArrayList<Stop>) {
        adapter = StopsAdapter(stopArrayList, this, this)
        binding.rvStops.setHasFixedSize(true)
        binding.rvStops.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(
            binding.rvStops.context,
            DividerItemDecoration.VERTICAL
        )
        binding.rvStops.addItemDecoration(dividerItemDecoration)
        binding.rvStops.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.getStops()
    }

    override fun onDeleteClicked(stop: Stop?) {
        Commons.showAlertDialog(
            "Delete Stop",
            "Are you sure you want to delete this stop?",
            this@StopsActivity
        ) {
            viewModel.deleteStop(stop?.id.toString())
        }
    }

    override fun onEditClicked(stop: Stop?) {
        startActivity(
            Intent(this@StopsActivity, AddStopsActivity::class.java).putExtra(
                "stop",
                stop
            )
        )
    }

    override fun onViewClicked(stop: Stop?) {
        val uri: String =
            String.format(
                Locale.ENGLISH,
                "geo:%f,%f",
                stop?.latitude?.toDouble(),
                stop?.longitude?.toDouble()
            )
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(intent)
    }
}