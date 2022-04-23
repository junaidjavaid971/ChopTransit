package ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.com.choptransit.R
import app.com.choptransit.adapters.StopTimelineAdapter
import app.com.choptransit.databinding.ActivityViewRouteDetailsBinding
import app.com.choptransit.extentions.dpToPx
import app.com.choptransit.extentions.getColorCompat
import app.com.choptransit.models.response.RouteData
import app.com.choptransit.utilities.Commons
import app.com.choptransit.utilities.TimelineAttributes
import app.com.choptransit.viewmodels.AdminViewModel
import com.github.vipulasri.timelineview.TimelineView
import java.util.ArrayList

class ViewRouteDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityViewRouteDetailsBinding
    lateinit var route: RouteData
    lateinit var attributes: TimelineAttributes
    lateinit var viewModel: AdminViewModel
    var routeID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_route_details)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AdminViewModel::class.java]

        routeID = intent.getStringExtra("routeID").toString()
        viewModel.getSpecificRoutes(routeID)
        setUpActionbar()

        setObservers()
        manageClicks()
    }

    private fun setObservers() {
        viewModel.dialogLiveData.observe(this) {
            if (it) {
                Commons.showProgress(this@ViewRouteDetailsActivity)
            } else {
                Commons.hideProgress()
            }
        }

        viewModel.routeResponseLiveData.observe(this) {
            if (it.code.equals("00")) {
                route = it.data.routesList[0]
                setupRoute()
            }
        }

        viewModel.baseResponseLiveData.observe(this) {
            if (it.code.equals("00")) {
                Commons.showToast(this@ViewRouteDetailsActivity, it.desc)
                onBackPressed()
            } else {
                Commons.showToast(this@ViewRouteDetailsActivity, it.desc)
            }
        }

        binding.tvAssignRoute.setOnClickListener {
            startActivity(
                Intent(
                    this@ViewRouteDetailsActivity,
                    AssignNewRouteActivity::class.java
                )
            )
        }
    }

    private fun setupRoute() {
        binding.tvRouteName.text = route.routeName
        binding.tvFare.text = "Fare: $" + route.fare
        initTimeLineAttributes()
        val mLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        binding.rvStops.apply {
            layoutManager = mLayoutManager
            adapter = StopTimelineAdapter(route.stops, attributes)
            binding.rvStops.visibility = View.VISIBLE
        }
    }

    fun initTimeLineAttributes() {
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
        (binding.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.str_RouteDetails)
        binding.actionBar.findViewById<View>(R.id.imgBack)
            .setOnClickListener { v: View -> onBackPressed() }
    }

    private fun manageClicks() {
        binding.ivDelete.setOnClickListener {
            Commons.showAlertDialog(
                "Delete Route",
                "Are you sure you want to delete this route?",
                this
            ) {
                viewModel.deleteRoute(route.id)
            }
        }
        binding.ivEdit.setOnClickListener {
            startActivity(
                Intent(this@ViewRouteDetailsActivity, AddRouteActivity::class.java)
                    .putExtra("route", route)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getSpecificRoutes(routeID)
    }
}