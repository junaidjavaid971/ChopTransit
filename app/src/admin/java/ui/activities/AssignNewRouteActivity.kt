package ui.activities

import adapters.AssignStopsAdapter
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.com.choptransit.R
import app.com.choptransit.databinding.ActivityAssignNewRouteBinding
import app.com.choptransit.extentions.dpToPx
import app.com.choptransit.extentions.getColorCompat
import app.com.choptransit.models.response.BusResponse
import app.com.choptransit.models.response.RouteData
import app.com.choptransit.models.response.Stop
import app.com.choptransit.utilities.Commons
import app.com.choptransit.utilities.TimelineAttributes
import app.com.choptransit.viewmodels.AdminViewModel
import com.github.vipulasri.timelineview.TimelineView
import java.util.*
import kotlin.collections.ArrayList

class AssignNewRouteActivity : AppCompatActivity(), AssignStopsAdapter.ArrivalTimeCallback {

    lateinit var binding: ActivityAssignNewRouteBinding
    lateinit var viewModel: AdminViewModel
    var selectedRoute: RouteData? = null
    var selectedBus: BusResponse.BusData? = null

    var departureTime = ""

    var assignedStops = ArrayList<Stop>()
    var updatedStops = ArrayList<Stop>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_assign_new_route)

        viewModel = ViewModelProvider(this)[AdminViewModel::class.java]

        setUpActionbar()
        setObservers()
        manageClicks()
    }

    private fun setUpActionbar() {
        (binding.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.str_assignNewRoute)
        binding.actionBar.findViewById<View>(R.id.imgBack)
            .setOnClickListener { v: View? -> onBackPressed() }
    }

    private fun setObservers() {
        viewModel.baseResponseLiveData.observe(this) {
            if (it.code.equals("00")) {
                Commons.showToast(this@AssignNewRouteActivity, it.desc)
                onBackPressed()
            } else {
                Commons.showToast(this@AssignNewRouteActivity, it.desc)
            }
        }

        viewModel.dialogLiveData.observe(this) {
            if (it) {
                Commons.showProgress(this@AssignNewRouteActivity)
            } else {
                Commons.hideProgress()
            }
        }
    }

    private fun manageClicks() {
        binding.tvSelectRoute.setOnClickListener {
            startActivityForResult(
                Intent(
                    this@AssignNewRouteActivity,
                    SelectRouteActivity::class.java
                ), 1232
            )
        }
        binding.tvSelectBus.setOnClickListener {
            startActivityForResult(
                Intent(
                    this@AssignNewRouteActivity,
                    SelectBusActivity::class.java
                ), 2321
            )
        }

        binding.btnAssignRoute.setOnClickListener {
            if (selectedRoute == null) {
                Commons.showToast(this@AssignNewRouteActivity, "Please select a route!")
            } else if (departureTime == "") {
                Commons.showToast(this@AssignNewRouteActivity, "Please set departure time!")
            } else if (checkEmptyStopTime()) {
                Commons.showToast(
                    this@AssignNewRouteActivity,
                    "Please select arrival time of all stops"
                )
            } else if (selectedBus == null) {
                Commons.showToast(this@AssignNewRouteActivity, "Please select a bus!")
            } else {
                selectedRoute?.stops = assignedStops
                viewModel.assignRoute(selectedRoute!!, selectedBus!!, departureTime)
            }
        }

        binding.tvSelectDepartureTime.setOnClickListener {
            val mcurrentTime: Calendar = Calendar.getInstance()
            val hour: Int = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute: Int = mcurrentTime.get(Calendar.MINUTE)
            val mTimePicker = TimePickerDialog(
                this@AssignNewRouteActivity,
                { timePicker, selectedHour, selectedMinute ->
                    run {
                        binding.tvSelectDepartureTime.text = "$selectedHour:$selectedMinute"
                        departureTime = "$selectedHour:$selectedMinute"
                    }
                },
                hour,
                minute,
                false
            )
            mTimePicker.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1232) {
            if (data != null) {
                selectedRoute = data.getSerializableExtra("route") as RouteData
                assignedStops = selectedRoute?.stops as ArrayList<Stop>
                initAdapter(selectedRoute?.stops!!)
                binding.tvSelectRoute.text = selectedRoute?.routeName
            }
        } else if (requestCode == 2321) {
            if (data != null) {
                selectedBus = data.getSerializableExtra("bus") as BusResponse.BusData
                binding.tvSelectBus.text = selectedBus?.busName + " (" + selectedBus?.busRegNo + ")"
            }
        }
    }

    private fun initAdapter(stops: ArrayList<Stop>) {
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
        val mLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        binding.rvStops.apply {
            layoutManager = mLayoutManager
            adapter = AssignStopsAdapter(
                stops,
                this@AssignNewRouteActivity,
                attributes,
                this@AssignNewRouteActivity
            )
            binding.rvStops.visibility = View.VISIBLE
            binding.view3.visibility = View.VISIBLE
        }
    }

    private fun checkEmptyStopTime(): Boolean {
        var isEmpty = false
        for (stop in assignedStops) {
            if (stop.arrivalTime.isEmpty()) {
                isEmpty = true
                break
            }
        }
        return isEmpty
    }

    override fun onArrivalTimeSelected(stop: Stop) {
        updateStopsList(stop)
    }

    private fun updateStopsList(stop: Stop) {
        for (s in assignedStops) {
            if (s.id == stop.id) {
                s.arrivalTime = stop.arrivalTime
                break
            }
        }
    }
}