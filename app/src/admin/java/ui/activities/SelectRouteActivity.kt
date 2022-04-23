package ui.activities

import adapters.RouteAdapter
import adapters.SelectRouteAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.com.choptransit.R
import app.com.choptransit.databinding.ActivitySelectRouteBinding
import app.com.choptransit.models.response.RouteData
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.AdminViewModel

class SelectRouteActivity : AppCompatActivity(), SelectRouteAdapter.SelectRouteCallback {

    lateinit var binding: ActivitySelectRouteBinding
    lateinit var viewModel: AdminViewModel
    lateinit var adapter: SelectRouteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_route)

        viewModel = ViewModelProvider(this)[AdminViewModel::class.java]

        setUpActionbar()
        setObservers()
        manageClicks()
        viewModel.getRoutes()
    }

    private fun setUpActionbar() {
        (binding.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.str_selectRoute)
        binding.actionBar.findViewById<View>(R.id.imgBack)
            .setOnClickListener { v: View? -> onBackPressed() }
    }


    private fun manageClicks() {
        binding.txtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                adapter.filter(s.toString())
            }
        })

        binding.imgCancel.setOnClickListener {
            binding.txtSearch.clearFocus()
            binding.txtSearch.text = null
            adapter.filter("")
        }
    }

    private fun setObservers() {
        viewModel.dialogLiveData.observe(this) {
            if (it) {
                Commons.showProgress(this@SelectRouteActivity)
            } else {
                Commons.hideProgress()
            }
        }

        viewModel.routeResponseLiveData.observe(this) {
            if (it.code.equals("00")) {
                val arrayList = it.data.routesList
                initRoutesRecyclerview(routeArrayList = arrayList)
            }
        }
    }

    private fun initRoutesRecyclerview(routeArrayList: ArrayList<RouteData>) {
        adapter = SelectRouteAdapter(routeArrayList, this, this)
        binding.rvRoutes.setHasFixedSize(true)
        binding.rvRoutes.layoutManager = LinearLayoutManager(this)
        binding.rvRoutes.adapter = adapter
    }

    override fun onRouteSelected(route: RouteData) {
        val intent = Intent()
        intent.putExtra("route", route)
        setResult(1232, intent)
        finish()
    }

}