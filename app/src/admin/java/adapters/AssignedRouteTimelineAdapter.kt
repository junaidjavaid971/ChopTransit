package adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.com.choptransit.R
import app.com.choptransit.models.response.Stop
import app.com.choptransit.models.response.StopsResponse
import app.com.choptransit.utilities.TimelineAttributes
import app.com.choptransit.utilities.VectorDrawableUtils

import com.github.vipulasri.timelineview.TimelineView


class AssignedRouteTimelineAdapter(
    private val stopsList: ArrayList<Stop>,
    private var mAttributes: TimelineAttributes
) : RecyclerView.Adapter<AssignedRouteTimelineAdapter.TimeLineViewHolder>() {

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

        val stop = stopsList[position]

        holder.timeline.marker = VectorDrawableUtils.getDrawable(
            holder.itemView.context,
            R.drawable.ic_marker_inactive,
            mAttributes.markerColor
        )

        holder.message.text = stop.stopName
        holder.tvArrivalTime.text = stop.arrivalTime
    }

    override fun getItemCount() = stopsList.size

    inner class TimeLineViewHolder(itemView: View, viewType: Int) :
        RecyclerView.ViewHolder(itemView) {
        var message: TextView = itemView.findViewById(R.id.text_timeline_title)
        var timeline: TimelineView = itemView.findViewById(R.id.timeline)
        var tvArrivalTime: TextView = itemView.findViewById(R.id.tv_arrivalTime)

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
