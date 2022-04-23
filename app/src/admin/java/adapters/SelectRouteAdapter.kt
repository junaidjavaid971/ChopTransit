package adapters

import android.content.Context
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
import app.com.choptransit.utilities.Commons
import app.com.choptransit.utilities.TimelineAttributes
import com.github.vipulasri.timelineview.TimelineView
import java.util.ArrayList

class SelectRouteAdapter(
    var arrayList: ArrayList<RouteData>,
    var context: Context,
    var callback: SelectRouteCallback
) :
    RecyclerView.Adapter<SelectRouteAdapter.ViewHolder>() {
    var tempList: ArrayList<RouteData>? = null
    fun filter(text: String) {
        var text = text
        if (tempList == null) {
            tempList = ArrayList(arrayList)
        }
        if (text.isEmpty()) {
            arrayList.clear()
            arrayList.addAll(tempList!!)
            tempList = null
        } else {
            val result = ArrayList<RouteData>()
            text = text.toLowerCase()
            //after clearing the array again you are using same array to find the items from
            for (route in tempList!!) {
                if (route.routeName.toLowerCase().contains(text)) {
                    result.add(route)
                }
            }
            //you have cleared all the contains here
            arrayList.clear()
            // and added only result related items here
            arrayList.addAll(result)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.layout_select_route_item, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val route = arrayList[position]
        holder.routeName.text = route.routeName
        holder.tvFare.text = "Fare: " + route.fare

        initTimeLineAttributes(holder, route)
        holder.itemView.setOnClickListener {
            Commons.showAlertDialog(
                "Confirm Route",
                "Are you sure you want to select this route? ",
                context
            ) {
                callback.onRouteSelected(route)
            }
        }

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
            adapter = StopTimelineAdapter(route.stops, attributes)
            holder.rvStops.visibility = View.VISIBLE
        }
    }

    public interface SelectRouteCallback {
        fun onRouteSelected(route: RouteData)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var routeName: TextView = itemView.findViewById(R.id.tvRouteName)
        var layoutRouteDetails: ConstraintLayout = itemView.findViewById(R.id.layoutRouteDetails)
        var tvFare: TextView = itemView.findViewById(R.id.tvFare)
        var rvStops: RecyclerView = itemView.findViewById(R.id.rvStops)
        var ivExpand: ImageView = itemView.findViewById(R.id.ivPlus)

    }
}