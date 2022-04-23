package adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.com.choptransit.R
import app.com.choptransit.models.response.CompanyResponse
import ui.activities.ViewCompanyDetailsActivity
import java.util.*

class CompanyAdapter(var arrayList: ArrayList<CompanyResponse.CompanyData>, var context: Context) :
    RecyclerView.Adapter<CompanyAdapter.ViewHolder>() {
    var tempList: ArrayList<CompanyResponse.CompanyData>? = null
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
            val result = ArrayList<CompanyResponse.CompanyData>()
            value = value.lowercase(Locale.getDefault())
            for (company in tempList!!) {
                if (company.companyName.lowercase(Locale.getDefault()).contains(value)) {
                    result.add(company)
                }
            }
            arrayList.clear()
            arrayList.addAll(result)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.layout_item_company, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val company = arrayList[position]
        holder.companyName.text = company.companyName

        holder.itemView.setOnClickListener {
            context.startActivity(
                Intent(
                    context,
                    ViewCompanyDetailsActivity::class.java
                ).putExtra("companyID", company.id)
            )
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var companyName: TextView = itemView.findViewById(R.id.tvCompanyName)

    }
}