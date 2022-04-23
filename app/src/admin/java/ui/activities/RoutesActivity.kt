package ui.activities

import adapters.RouteAdapter
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
import app.com.choptransit.databinding.ActivityRoutesBinding
import app.com.choptransit.models.response.RouteData
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.AdminViewModel

class RoutesActivity : AppCompatActivity(), RouteAdapter.RouteButtonsCallback {

    lateinit var binding: ActivityRoutesBinding
    lateinit var viewModel: AdminViewModel
    lateinit var adapter: RouteAdapter
    lateinit var arrayList: ArrayList<RouteData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_routes)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AdminViewModel::class.java)
        binding.routesViewModel = viewModel

        setUpActionbar()
        setObservers()
        manageClicks()
        viewModel.getRoutes()
    }

    private fun setUpActionbar() {
        (binding.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.str_Routes)
        binding.actionBar.findViewById<View>(R.id.imgBack)
            .setOnClickListener { v: View? -> onBackPressed() }
    }

    private fun setObservers() {
        viewModel.dialogLiveData.observe(this) {
            if (it) {
                Commons.showProgress(this@RoutesActivity)
            } else {
                Commons.hideProgress()
            }
        }

        viewModel.routeResponseLiveData.observe(this) {
            if (it.code.equals("00")) {
                arrayList = it.data.routesList
                initRoutesRecyclerview(routeArrayList = arrayList)
            }
        }
    }

    private fun manageClicks() {
        binding.btnAddRoute.setOnClickListener {
            startActivity(Intent(this@RoutesActivity, AddRouteActivity::class.java))
        }

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

    private fun initRoutesRecyclerview(routeArrayList: ArrayList<RouteData>) {
        adapter = RouteAdapter(routeArrayList, this , this)
        binding.rvRoutes.setHasFixedSize(true)
        binding.rvRoutes.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(
            binding.rvRoutes.context,
            DividerItemDecoration.VERTICAL
        )
        binding.rvRoutes.addItemDecoration(dividerItemDecoration)
        binding.rvRoutes.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.getRoutes()
    }

    override fun onEditClicked(route: RouteData) {

    }

    override fun onDeleteClicked(route: RouteData) {

    }

    override fun onAssignClicked(route: RouteData) {

    }
}