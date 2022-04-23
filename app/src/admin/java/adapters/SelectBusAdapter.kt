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
import app.com.choptransit.models.response.BusResponse
import app.com.choptransit.utilities.Commons
import ui.activities.ViewBusDetailsActivity
import ui.activities.ViewRouteDetailsActivity
import java.util.*

class SelectBusAdapter(
    var arrayList: ArrayList<BusResponse.BusData>,
    var callback: BusSelectedCallback,
    var context: Context
) :
    RecyclerView.Adapter<SelectBusAdapter.ViewHolder>() {
    var tempList: ArrayList<BusResponse.BusData>? = null
    fun filter(text: String) {
        var value = text
        if (tempList == null) {
            tempList = ArrayList(arrayList)
        }
        if (value.isEmpty()) {
            arrayList.clear()
            arrayList.addAll(tempList!!)
            tempList = null
        } else {
            val result = ArrayList<BusResponse.BusData>()
            value = value.lowercase(Locale.getDefault())
            for (bus in tempList!!) {
                if (bus.busName.lowercase(Locale.getDefault()).contains(value)) {
                    result.add(bus)
                }
            }
            arrayList.clear()
            arrayList.addAll(result)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.layout_item_bus, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bus = arrayList[position]
        holder.busName.text = bus.busName + " (" + bus.busRegNo + ")"

        holder.ivArrow.visibility = View.GONE

        holder.itemView.setOnClickListener {
            if (bus.assignedTo.isEmpty() || bus.assignedTo.equals("0")) {
                Commons.showAlertDialog(
                    "Confirm Bus",
                    "Are you sure you want to select this bus?",
                    context
                ) {
                    callback.onBusSelected(bus)
                }
            } else {
                Commons.showAlertDialog(
                    "Confirm Bus",
                    "This bus is already assigned to another driver. Reassigning will remove this bus from that driver. Are you sure you want to continue?",
                    context
                ) {
                    callback.onBusSelected(bus)
                }
            }
        }
    }

    public interface BusSelectedCallback {
        fun onBusSelected(bus: BusResponse.BusData)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var busName: TextView = itemView.findViewById(R.id.tvBusName)
        var ivArrow: ImageView = itemView.findViewById(R.id.ivArrow)
    }
}