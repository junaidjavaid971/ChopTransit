package ui.activities

import androidx.appcompat.app.AppCompatActivity
import app.com.choptransit.viewmodels.AdminViewModel
import app.com.choptransit.utilities.TimelineAttributes
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import app.com.choptransit.R
import androidx.lifecycle.ViewModelProvider
import android.widget.TextView
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.com.choptransit.adapters.StopTimelineAdapter
import app.com.choptransit.databinding.ActivityAddRoutesBinding
import app.com.choptransit.extentions.dpToPx
import app.com.choptransit.extentions.getColorCompat
import app.com.choptransit.models.response.RouteData
import app.com.choptransit.models.response.Stop
import app.com.choptransit.utilities.Commons
import com.github.vipulasri.timelineview.TimelineView
import com.google.android.gms.maps.model.LatLng

class AddRouteActivity : AppCompatActivity() {
    private var binding: ActivityAddRoutesBinding? = null
    private var viewModel: AdminViewModel? = null
    private var attributes: TimelineAttributes? = null
    private lateinit var mLayoutManager: LinearLayoutManager
    lateinit var route: RouteData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_routes)
        setContentView(binding?.root)
        viewModel = ViewModelProvider(this).get(AdminViewModel::class.java)
        binding?.addAdminViewModel = viewModel

        initTimeLineAttributes()
        setUpActionbar()
        manageClicks()
        setObservers()

        if (intent.hasExtra("route")) {
            route = intent.getSerializableExtra("route") as RouteData
            setupRouteData()
        }
    }

    private fun setupRouteData() {
        binding?.btnAddRoute?.text = getString(R.string.str_updateRoute)
        (binding?.actionBar?.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.str_updateRoute)
        viewModel?.routeModel?.routeName = route.routeName
        viewModel?.routeModel?.fare = route.fare
        viewModel?.routeModel?.id = route.id
        viewModel?.routeModel?.stopsList = route.stops
        viewModel?.routeModel?.updateRoute = true

        initAdapter(viewModel?.routeModel?.stopsList!!)

        binding?.tvChangeStops?.setOnClickListener {
            startActivityForResult(
                Intent(
                    this@AddRouteActivity,
                    SelectStopsActivity::class.java
                ).putExtra("stops", viewModel?.routeModel?.stopsList!!), 1121
            )
        }
    }

    private fun setObservers() {
        viewModel?.baseResponseLiveData?.observe(this@AddRouteActivity) {
            if (it.code == "00") {
                Commons.showToast(this@AddRouteActivity, it.desc)
                onBackPressed()
            } else {
                Commons.showToast(this@AddRouteActivity, it.desc)
            }
        }

        viewModel?.dialogLiveData?.observe(this@AddRouteActivity) {
            if (it) {
                Commons.showProgress(this@AddRouteActivity)
            } else {
                Commons.hideProgress()
            }
        }

        viewModel?.errorLiveData?.observe(this@AddRouteActivity) {
            Commons.showToast(this@AddRouteActivity, it)
        }
    }

    private fun initTimeLineAttributes() {
        attributes = TimelineAttributes(
            markerSize = dpToPx(15f),
            markerColor = getColorCompat(R.color.primaryColor),
            markerInCenter = true,
            markerLeftPadding = dpToPx(0f),
            markerTopPadding = dpToPx(0f),
            markerRightPadding = dpToPx(0f),
            markerBottomPadding = dpToPx(0f),
            linePadding = dpToPx(2f),
            startLineColor = getColorCompat(R.color.primaryColor),
            endLineColor = getColorCompat(R.color.primaryColor),
            lineStyle = TimelineView.LineStyle.DASHED,
            lineWidth = dpToPx(2f),
            lineDashWidth = dpToPx(4f),
            lineDashGap = dpToPx(2f)
        )
    }

    private fun setUpActionbar() {
        (binding!!.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.str_addRoute)
        binding!!.actionBar.findViewById<View>(R.id.imgBack)
            .setOnClickListener { v: View? -> onBackPressed() }
    }

    private fun manageClicks() {
        binding?.tvAddStops?.setOnClickListener {
            startActivityForResult(
                Intent(
                    this@AddRouteActivity,
                    SelectStopsActivity::class.java
                ), 1121
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1121) {
            if (data != null) {
                viewModel?.routeModel?.stopsList =
                    data.getSerializableExtra("stopsList") as ArrayList<Stop>
                initAdapter(viewModel?.routeModel?.stopsList!!)
            }
        }
    }

    private fun initAdapter(stops: ArrayList<Stop>) {
        mLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        binding?.rvStops?.apply {
            layoutManager = mLayoutManager
            adapter = StopTimelineAdapter(stops, attributes!!)
            binding?.rvStops?.visibility = View.VISIBLE
            binding?.animStops?.visibility = View.GONE
            binding?.tvAddStops?.visibility = View.GONE
            binding?.tvChangeStops?.visibility = View.VISIBLE
        }
    }
}