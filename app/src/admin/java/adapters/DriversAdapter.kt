package adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.com.choptransit.R
import app.com.choptransit.models.response.DriverData
import ui.activities.ViewDriverDetailsActivity
import java.util.*

class DriversAdapter(
    var arrayList: ArrayList<DriverData>,
    var context: Context,
    var isRequest: Boolean
) :
    RecyclerView.Adapter<DriversAdapter.ViewHolder>() {
    var tempList: ArrayList<DriverData>? = null
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
            val result = ArrayList<DriverData>()
            value = value.lowercase(Locale.getDefault())
            for (driver in tempList!!) {
                if (driver.getFirstName().lowercase(Locale.getDefault())
                        .contains(value) || driver.getLastName().lowercase(Locale.getDefault())
                        .contains(value)
                ) {
                    result.add(driver)
                }
            }
            arrayList.clear()
            arrayList.addAll(result)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.layout_item_driver, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val driver = arrayList[position]
        holder.driverName.text = driver.getFirstName() + " " + driver.getLastName()

        holder.itemView.setOnClickListener {
            context.startActivity(
                Intent(
                    context,
                    ViewDriverDetailsActivity::class.java
                ).putExtra("driver", driver).putExtra("isRequest", isRequest)
            )
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var driverName: TextView = itemView.findViewById(R.id.tvDriverName)

    }
}