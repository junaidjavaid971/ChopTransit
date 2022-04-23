package adapters

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import app.com.choptransit.R
import app.com.choptransit.models.response.AssignRouteData
import app.com.choptransit.models.response.RouteData
import app.com.choptransit.models.response.Stop
import app.com.choptransit.models.response.StopsResponse
import app.com.choptransit.utilities.Commons
import app.com.choptransit.utilities.TimelineAttributes
import app.com.choptransit.utilities.VectorDrawableUtils

import com.github.vipulasri.timelineview.TimelineView
import ui.activities.RideBookActivity


class BookRouteTimelineAdapter(
    private val route: AssignRouteData,
    private var mAttributes: TimelineAttributes,
    private var context: Context
) : RecyclerView.Adapter<BookRouteTimelineAdapter.TimeLineViewHolder>() {

    private lateinit var mLayoutInflater: LayoutInflater

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        mLayoutInflater = LayoutInflater.from(parent.context);
        val view =
            mLayoutInflater.inflate(R.layout.item_assign_stops_timeline, parent, false)
        return TimeLineViewHolder(view, viewType)
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {

        val stop = route.route.stops[position]

        holder.timeline.marker = VectorDrawableUtils.getDrawable(
            holder.itemView.context,
            R.drawable.ic_marker_inactive,
            mAttributes.markerColor
        )

        holder.message.text = (position + 1).toString() + ". " + stop.stopName
        holder.tvArrivalTime.text = stop.arrivalTime

        holder.ivGo.setOnClickListener {
            Commons.showAlertDialog(
                "Confirm this stop",
                "Are you sure you want to book from this stop?",
                context
            ) {
                context.startActivity(
                    Intent(
                        context,
                        RideBookActivity::class.java
                    ).putExtra("route", route).putExtra("stop", stop)
                )
            }
        }
    }

    override fun getItemCount() = route.route.stops.size

    inner class TimeLineViewHolder(itemView: View, viewType: Int) :
        RecyclerView.ViewHolder(itemView) {
        var message: TextView = itemView.findViewById(R.id.text_timeline_title)
        var timeline: TimelineView = itemView.findViewById(R.id.timeline)
        var tvArrivalTime: TextView = itemView.findViewById(R.id.tv_arrivalTime)
        var ivGo: ImageView = itemView.findViewById(R.id.ivGo)

        init {

            timeline.initLine(viewType)
            timeline.markerSize = mAttributes.markerSize
            timeline.setMarkerColor(mAttributes.markerColor)
            timeline.isMarkerInCenter = mAttributes.markerInCenter
            timeline.markerPaddingLeft = mAttributes.markerLeftPadding
            timeline.markerPaddingTop = mAttributes.markerTopPadding
            timeline.markerPaddingRight = mAttributes.markerRightPadding
            timeline.markerPaddingBottom = mAttributes.markerBottomPadding
            timeline.linePadding = mAttributes.linePadding

            timeline.lineWidth = mAttributes.lineWidth
            timeline.setStartLineColor(mAttributes.startLineColor, viewType)
            timeline.setEndLineColor(mAttributes.endLineColor, viewType)
            timeline.lineStyle = mAttributes.lineStyle
            timeline.lineStyleDashLength = mAttributes.lineDashWidth
            timeline.lineStyleDashGap = mAttributes.lineDashGap
        }
    }

}
