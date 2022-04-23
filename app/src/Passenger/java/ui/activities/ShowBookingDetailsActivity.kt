package ui.activities

import adapters.AssignedRouteTimelineAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.com.choptransit.R
import app.com.choptransit.databinding.ActivityShowBookingDetailsBinding
import app.com.choptransit.extentions.dpToPx
import app.com.choptransit.extentions.getColorCompat
import app.com.choptransit.models.response.AssignRouteData
import app.com.choptransit.utilities.TimelineAttributes
import app.com.choptransit.viewmodels.AdminViewModel
import com.github.vipulasri.timelineview.TimelineView
import ui.fragments.ConfirmedBookingMapFragment
import ui.fragments.StopsMapFragment

class ShowBookingDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityShowBookingDetailsBinding
    lateinit var viewModel: AdminViewModel
    lateinit var route: AssignRouteData
    var active = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_booking_details)
        viewModel = ViewModelProvider(this)[AdminViewModel::class.java]

        route = intent.getSerializableExtra("route") as AssignRouteData
        active = intent.getBooleanExtra("active", false)

        binding.bottomSheet.findViewById<TextView>(R.id.tvRouteName).text = route.route.routeName
        binding.bottomSheet.findViewById<TextView>(R.id.tvFare).text = "$ " + route.route.fare
        binding.bottomSheet.findViewById<TextView>(R.id.tvBusInfo).text =
            route.bus.busName + " (" + route.bus.busRegNo + ")"
        binding.bottomSheet.findViewById<TextView>(R.id.tvDriverInfo).text =
            route.bus.driver.firstName + " " + route.bus.driver.lastName
        binding.bottomSheet.findViewById<TextView>(R.id.tvRouteName).text =
            route.route.routeName
        binding.bottomSheet.findViewById<TextView>(R.id.tvStopName).text =
            "Departure time: " + route.departureTime

        setFragment()
        initTimeLineAttributes(route)
    }

    private fun initTimeLineAttributes(route: AssignRouteData) {
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
            LinearLayoutManager(this@ShowBookingDetailsActivity, RecyclerView.VERTICAL, false)

        binding.bottomSheet.findViewById<RecyclerView>(R.id.rvStops).apply {
            layoutManager = mLayoutManager
            adapter = AssignedRouteTimelineAdapter(
                route.route.stops,
                attributes,
                this@ShowBookingDetailsActivity
            )
            this.visibility = View.VISIBLE
        }
    }

    private fun setFragment() {
        if (active) {
            val ft = supportFragmentManager.beginTransaction()

            ft.replace(R.id.flMap, ConfirmedBookingMapFragment(route))
            ft.commit()
        } else {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.flMap, StopsMapFragment(route))
            ft.commit()
        }
    }
}