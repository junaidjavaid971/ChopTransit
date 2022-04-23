package app.com.choptransit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.com.choptransit.R
import app.com.choptransit.models.response.BookingResponseData
import app.com.choptransit.models.response.CompanyResponse
import app.com.choptransit.utilities.Commons
import java.util.*

class ConfirmedPassengersAdapter(
    var arrayList: ArrayList<BookingResponseData>,
    var context: Context,
    var callback: BookingControlsCallback
) :
    RecyclerView.Adapter<ConfirmedPassengersAdapter.ViewHolder>() {
    var tempList: ArrayList<BookingResponseData>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.layout_confirmed_passengers, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booking = arrayList[position]
        holder.tvPassengerName.text = booking.passenger.name
        holder.tvStopName.text = booking.stopName
        holder.tvFare.text = "$" + booking.assignedRoute.route.fare

        holder.btnApprove.setOnClickListener {
            callback.onPickPassengerClicked(booking)
        }

        if (booking.status.equals("active", ignoreCase = true)) {
            holder.btnEndRide.visibility = View.VISIBLE
            holder.btnApprove.visibility = View.GONE
            holder.btnReject.visibility = View.GONE
        } else {
            holder.btnEndRide.visibility = View.GONE
            holder.btnApprove.visibility = View.VISIBLE
            holder.btnReject.visibility = View.VISIBLE
        }
        holder.btnEndRide.setOnClickListener {
            Commons.showAlertDialog(
                "End Ride",
                "Are you sure you want to end this ride?",
                context
            ) {
                callback.onEndRideClicked(booking)
            }
        }
        holder.btnReject.setOnClickListener {
            Commons.showAlertDialog(
                "Reject Ride",
                "Are you sure you want to miss this passenger?",
                context
            ) {
                callback.onMissPassengerClicked(booking)
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    public interface BookingControlsCallback {
        fun onPickPassengerClicked(booking: BookingResponseData);
        fun onMissPassengerClicked(booking: BookingResponseData);
        fun onEndRideClicked(booking: BookingResponseData);
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvPassengerName: TextView = itemView.findViewById(R.id.tvPassengerName)
        var tvStopName: TextView = itemView.findViewById(R.id.tvStopName)
        var tvFare: TextView = itemView.findViewById(R.id.tvFare)
        var btnApprove: Button = itemView.findViewById(R.id.btnApprove)
        var btnReject: Button = itemView.findViewById(R.id.btnReject)
        var btnEndRide: Button = itemView.findViewById(R.id.btnEndRide)

    }
}