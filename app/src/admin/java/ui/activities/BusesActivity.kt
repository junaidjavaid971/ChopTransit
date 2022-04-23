package ui.activities

import adapters.BusAdapter
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
import app.com.choptransit.databinding.ActivityBusesBinding
import app.com.choptransit.models.response.BusResponse
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.AdminViewModel

class BusesActivity : AppCompatActivity() {

    lateinit var binding: ActivityBusesBinding
    lateinit var viewModel: AdminViewModel
    lateinit var adapter: BusAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_buses)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AdminViewModel::class.java)
        setUpActionbar()
        manageClicks()
        setObservers()

        viewModel.getBuses()
    }

    private fun setUpActionbar() {
        (binding.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.str_buses)
        binding.actionBar.findViewById<View>(R.id.imgBack)
            .setOnClickListener { v: View? -> onBackPressed() }
    }

    private fun manageClicks() {
        binding.btnAddBus.setOnClickListener {
            startActivity(Intent(this@BusesActivity, AddBusActivity::class.java))
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

    private fun setObservers() {
        viewModel.dialogLiveData.observe(this) {
            if (it) {
                Commons.showProgress(this@BusesActivity)
            } else {
                Commons.hideProgress()
            }
        }

        viewModel.busResponseLiveData.observe(this) {
            if (it.code == "00") {
                initRoutesRecyclerview(it.data.busesList)
            }
        }
    }

    private fun initRoutesRecyclerview(busArrayList: ArrayList<BusResponse.BusData>) {
        adapter = BusAdapter(busArrayList, this)
        binding.rvBuses.setHasFixedSize(true)
        binding.rvBuses.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(
            binding.rvBuses.context,
            DividerItemDecoration.VERTICAL
        )
        binding.rvBuses.addItemDecoration(dividerItemDecoration)
        binding.rvBuses.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.getBuses()
    }
}