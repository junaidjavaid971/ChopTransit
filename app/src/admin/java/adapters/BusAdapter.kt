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
import ui.activities.ViewBusDetailsActivity
import ui.activities.ViewRouteDetailsActivity
import java.util.*

class BusAdapter(var arrayList: ArrayList<BusResponse.BusData>, var context: Context) :
    RecyclerView.Adapter<BusAdapter.ViewHolder>() {
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
        holder.busName.text = bus.busName

        holder.itemView.setOnClickListener {
            context.startActivity(
                Intent(
                    context,
                    ViewBusDetailsActivity::class.java
                ).putExtra("busID", bus.id)
            )
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var busName: TextView = itemView.findViewById(R.id.tvBusName)

    }
}