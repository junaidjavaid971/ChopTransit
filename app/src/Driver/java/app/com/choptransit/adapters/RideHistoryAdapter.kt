package app.com.choptransit.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.com.choptransit.R
import app.com.choptransit.models.response.BookingResponseData

class RideHistoryAdapter(
    var arrayList: ArrayList<BookingResponseData>,
    var context: Context
) :
    RecyclerView.Adapter<RideHistoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.ride_history_layout, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booking = arrayList[position]
        holder.tvBusName.text = booking.stopName
        holder.tvBusNumber.text = booking.assignedRoute.bus.busRegNo
        holder.tvStopNmae.text = booking.passenger.name
        holder.tvFare.text = booking.assignedRoute.route.fare
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvStopNmae: TextView = itemView.findViewById(R.id.tvStopName)
        var tvFare: TextView = itemView.findViewById(R.id.tvPrice)

        var tvBusName: TextView = itemView.findViewById(R.id.tvBusName)
        var tvBusNumber: TextView = itemView.findViewById(R.id.tvBusNumber)

    }
}