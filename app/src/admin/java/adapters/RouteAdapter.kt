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
import ui.activities.ViewRouteDetailsActivity
import java.util.ArrayList

class RouteAdapter(var arrayList: ArrayList<RouteData>, var context: Context, var callback : RouteButtonsCallback) :
    RecyclerView.Adapter<RouteAdapter.ViewHolder>() {
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
        val listItem = layoutInflater.inflate(R.layout.layout_item_route, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val route = arrayList[position]
        holder.routeName.text = route.routeName

        holder.itemView.setOnClickListener {
            context.startActivity(
                Intent(
                    context,
                    ViewRouteDetailsActivity::class.java
                ).putExtra("routeID", route.id)
            )
        }
    }
    

    public interface RouteButtonsCallback {
        fun onEditClicked(route: RouteData)
        fun onDeleteClicked(route: RouteData)
        fun onAssignClicked(route: RouteData)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var routeName: TextView = itemView.findViewById(R.id.tvRouteName)

    }
}