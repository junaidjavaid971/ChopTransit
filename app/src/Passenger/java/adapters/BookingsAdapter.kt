package adapters

import android.content.Context
import android.content.Intent
import app.com.choptransit.models.response.RouteData
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import app.com.choptransit.R
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import app.com.choptransit.adapters.StopTimelineAdapter
import app.com.choptransit.extentions.dpToPx
import app.com.choptransit.extentions.getColorCompat
import app.com.choptransit.models.response.AssignRouteData
import app.com.choptransit.models.response.BookingResponseData
import app.com.choptransit.utilities.TimelineAttributes
import com.github.vipulasri.timelineview.TimelineView
import ui.activities.BookingQRCodeActivity
import ui.activities.ShowBookingDetailsActivity
import ui.activities.ShowRouteDetailsActivity
import java.util.ArrayList

class BookingsAdapter(
    var arrayList: ArrayList<BookingResponseData>,
    var context: Context,
    var isConfirmed: Boolean
) :
    RecyclerView.Adapter<BookingsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.layout_select_route_item, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booking = arrayList[position]
        holder.routeName.text = booking.assignedRoute.route.routeName
        holder.tvFare.text = "Fare: " + booking.assignedRoute.route.fare
        holder.tvBusName.text =
            booking.assignedRoute.bus.busName + " (" + booking.assignedRoute.bus.busRegNo + ")"
        holder.tvBusType.text = booking.assignedRoute.bus.busType

        holder.tvDriverInfo.text =
            booking.assignedRoute.bus.driver.firstName + " " + booking.assignedRoute.bus.driver.lastName

        initTimeLineAttributes(holder, booking.assignedRoute.route)

        holder.ivExpand.setOnClickListener {
            if (holder.layoutRouteDetails.visibility == View.GONE) {
                holder.ivExpand.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_minus
                    )
                )
                holder.layoutRouteDetails.visibility = View.VISIBLE
            } else {
                holder.layoutRouteDetails.visibility = View.GONE
                holder.ivExpand.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_plus
                    )
                )
            }
        }

        holder.itemView.setOnClickListener {
            if (isConfirmed) {
                context.startActivity(
                    Intent(
                        context,
                        BookingQRCodeActivity::class.java
                    ).putExtra("route", booking.assignedRoute).putExtra("booking", booking)
                )
            } else {
                context.startActivity(
                    Intent(
                        context,
                        ShowBookingDetailsActivity::class.java
                    ).putExtra("route", booking.assignedRoute).putExtra("active", true)
                )
            }
        }

        if (position == 0) {
            holder.ivExpand.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_minus
                )
            )
            holder.layoutRouteDetails.visibility = View.VISIBLE
        }
    }


    private fun initTimeLineAttributes(holder: ViewHolder, route: RouteData) {
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

        val mLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        holder.rvStops.apply {
            layoutManager = mLayoutManager
            adapter = AssignedRouteTimelineAdapter(route.stops, attributes, context)
            holder.rvStops.visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var routeName: TextView = itemView.findViewById(R.id.tvRouteName)
        var tvFare: TextView = itemView.findViewById(R.id.tvFare)
        var ivExpand: ImageView = itemView.findViewById(R.id.ivPlus)
        var layoutRouteDetails: ConstraintLayout =
            itemView.findViewById(R.id.layoutRouteDetails)
        var rvStops: RecyclerView = itemView.findViewById(R.id.rvStops)
        var tvBusName: TextView = itemView.findViewById(R.id.tvBusName)
        var tvBusType: TextView = itemView.findViewById(R.id.tvBusType)
        var tvDriverInfo: TextView = itemView.findViewById(R.id.tvDriverInfo)

    }
}