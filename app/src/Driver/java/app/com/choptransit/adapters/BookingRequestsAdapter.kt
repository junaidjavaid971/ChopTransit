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

class BookingRequestsAdapter(
    var arrayList: ArrayList<BookingResponseData>,
    var context: Context,
    var callback: BookingControlsCallback
) :
    RecyclerView.Adapter<BookingRequestsAdapter.ViewHolder>() {
    var tempList: ArrayList<BookingResponseData>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.layout_ride_request, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booking = arrayList[position]
        holder.tvPassengerName.text = booking.passenger.name
        holder.tvStopName.text = booking.stopName

        holder.btnApprove.setOnClickListener {
            Commons.showAlertDialog(
                "Approve Ride",
                "Are you sure you want to approve this booking request?",
                context
            ) {
                callback.onApproveClicked(booking)
            }
        }
        holder.btnReject.setOnClickListener {
            Commons.showAlertDialog(
                "Reject Ride",
                "Are you sure you want to reject this booking request?",
                context
            ) {
                callback.onRejectClicked(booking)
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    public interface BookingControlsCallback {
        fun onApproveClicked(booking: BookingResponseData);
        fun onRejectClicked(booking: BookingResponseData);
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvPassengerName: TextView = itemView.findViewById(R.id.tvPassengerName)
        var tvStopName: TextView = itemView.findViewById(R.id.tvStopName)
        var btnApprove: Button = itemView.findViewById(R.id.btnApprove)
        var btnReject: Button = itemView.findViewById(R.id.btnReject)

    }
}