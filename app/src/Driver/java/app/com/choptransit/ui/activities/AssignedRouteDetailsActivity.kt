package app.com.choptransit.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.com.choptransit.R
import app.com.choptransit.adapters.AssignedRouteTimelineAdapter
import app.com.choptransit.databinding.ActivityAssignedRouteDetailsBinding
import app.com.choptransit.extentions.dpToPx
import app.com.choptransit.extentions.getColorCompat
import app.com.choptransit.models.response.DriverData
import app.com.choptransit.models.response.RouteData
import app.com.choptransit.utilities.Commons
import app.com.choptransit.utilities.TimelineAttributes
import app.com.choptransit.viewmodels.AdminViewModel
import com.github.vipulasri.timelineview.TimelineView

class AssignedRouteDetailsActivity : BaseActivity() {
    lateinit var binding: ActivityAssignedRouteDetailsBinding
    lateinit var viewModel: AdminViewModel
    lateinit var driver: DriverData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_assigned_route_details)
        viewModel = ViewModelProvider(this)[AdminViewModel::class.java]
        driver = intent.getSerializableExtra("driver") as DriverData

        setUpActionbar()
        setObservers()
        viewModel.getRouteByDriverID(driver.id)

    }

    private fun setUpActionbar() {
        (binding.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.assign_routeDetails)
        binding.actionBar.findViewById<View>(R.id.imgBack)
            .setOnClickListener { v: View? -> onBackPressed() }
    }

    private fun setObservers() {
        viewModel.dialogLiveData.observe(this) {
            if (it) {
                Commons.showProgress(this@AssignedRouteDetailsActivity)
            } else {
                Commons.hideProgress()
            }
        }

        viewModel.assignRouteReponseLiveData.observe(this) {
            if (it.code.equals("00")) {
                if (it.data.isEmpty()) {
                    binding.animationLayout.visibility = View.VISIBLE
                    binding.scrollView.visibility = View.GONE
                    return@observe
                }
                val assignedRoute = it.data[0]
                binding.tvRouteName.text = assignedRoute.route.routeName
                binding.tvFare.text = "Fare: $" + assignedRoute.route.fare
                binding.tvBusName.text =
                    assignedRoute.bus.busName + " (" + assignedRoute.bus.busRegNo + ")"
                binding.tvBusType.text = assignedRoute.bus.busType

                binding.tvDriverInfo.text =
                    assignedRoute.bus.driver.firstName + " " + assignedRoute.bus.driver.lastName

                initTimeLineAttributes(assignedRoute.route)

                binding.animationLayout.visibility = View.GONE
                binding.scrollView.visibility = View.VISIBLE
            }
        }
    }


    private fun initTimeLineAttributes(route: RouteData) {
        val attributes = TimelineAttributes(
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

        val mLayoutManager =
            LinearLayoutManager(this@AssignedRouteDetailsActivity, RecyclerView.VERTICAL, false)

        binding.rvStops.apply {
            layoutManager = mLayoutManager
            adapter = AssignedRouteTimelineAdapter(route.stops, attributes, context)
            binding.rvStops.visibility = View.VISIBLE
        }

    }


}