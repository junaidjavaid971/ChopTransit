package ui.activities

import adapters.DriversAdapter
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
import app.com.choptransit.databinding.ActivityDriverRequestsBinding
import app.com.choptransit.models.response.DriverData
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.AdminViewModel

class DriverRequestsActivity : AppCompatActivity() {

    lateinit var binding: ActivityDriverRequestsBinding
    lateinit var viewModel: AdminViewModel
    lateinit var adapter: DriversAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver_requests)
        viewModel = ViewModelProvider(this).get(AdminViewModel::class.java)

        setObservers()
        manageClicks()
        setUpActionbar()
        viewModel.getDriverRequests()
    }

    private fun setUpActionbar() {
        (binding.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.str_driverRequests)
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
        viewModel.dialogLiveData?.observe(this@DriverRequestsActivity) {
            if (it) {
                Commons.showProgress(this@DriverRequestsActivity)
            } else {
                Commons.hideProgress()
            }
        }

        viewModel.driversResponseLiveData.observe(this@DriverRequestsActivity) {
            if (it.code == "00") {
                if (it.data.isEmpty()) {
                    binding.tvNoResultFound.visibility = View.VISIBLE
                    binding.rvDriverRequests.visibility = View.GONE
                } else {
                    binding.tvNoResultFound.visibility = View.GONE
                    binding.rvDriverRequests.visibility = View.VISIBLE
                    initDriverRequestsRecyclerview(it.data)
                }
            }
        }
    }

    private fun initDriverRequestsRecyclerview(driversArrayList: ArrayList<DriverData>) {
        adapter = DriversAdapter(driversArrayList, this, true)
        binding.rvDriverRequests.setHasFixedSize(true)
        binding.rvDriverRequests.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(
            binding.rvDriverRequests.context,
            DividerItemDecoration.VERTICAL
        )
        binding.rvDriverRequests.addItemDecoration(dividerItemDecoration)
        binding.rvDriverRequests.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.getDriverRequests()
    }
}