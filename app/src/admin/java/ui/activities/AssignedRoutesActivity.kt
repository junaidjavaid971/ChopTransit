package ui.activities

import adapters.AssignedRoutesAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.com.choptransit.R
import app.com.choptransit.databinding.ActivityAssignedRoutesBinding
import app.com.choptransit.models.response.AssignRouteData
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.AdminViewModel
import java.util.ArrayList

class AssignedRoutesActivity : AppCompatActivity() {
    lateinit var binding: ActivityAssignedRoutesBinding
    lateinit var viewModel: AdminViewModel
    lateinit var adapter: AssignedRoutesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_assigned_routes)

        viewModel = ViewModelProvider(this).get(AdminViewModel::class.java)

        setUpActionbar()
        setObservers()
        manageClicks()
        viewModel.getAssignedRoutes()
    }

    private fun setUpActionbar() {
        (binding.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.assign_routes)
        binding.actionBar.findViewById<View>(R.id.imgBack)
            .setOnClickListener { v: View? -> onBackPressed() }
    }

    private fun setObservers() {
        viewModel.dialogLiveData.observe(this) {
            if (it) {
                Commons.showProgress(this@AssignedRoutesActivity)
            } else {
                Commons.hideProgress()
            }
        }

        viewModel.assignRouteReponseLiveData.observe(this) {
            if (it.code.equals("00")) {
                initRoutesRecyclerview(it.data)
            }
        }
    }

    private fun manageClicks() {
        binding.btnAssignRoute.setOnClickListener {
            startActivity(Intent(this@AssignedRoutesActivity, AssignNewRouteActivity::class.java))
        }
    }

    private fun initRoutesRecyclerview(routeArrayList: ArrayList<AssignRouteData>) {
        adapter = AssignedRoutesAdapter(routeArrayList, this@AssignedRoutesActivity)
        binding.rvAssignedRoutes.setHasFixedSize(true)
        binding.rvAssignedRoutes.layoutManager = LinearLayoutManager(this)
        binding.rvAssignedRoutes.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAssignedRoutes()
    }
}