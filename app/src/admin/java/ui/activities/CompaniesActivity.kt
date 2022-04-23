package ui.activities

import adapters.CompanyAdapter
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
import app.com.choptransit.databinding.ActivityCompaniesBinding
import app.com.choptransit.models.response.CompanyResponse
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.AdminViewModel

class CompaniesActivity : AppCompatActivity() {

    lateinit var binding: ActivityCompaniesBinding
    lateinit var viewModel: AdminViewModel

    lateinit var adapter: CompanyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_companies)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AdminViewModel::class.java)
        setUpActionbar()
        setObservers()
        manageClicks()
        viewModel.getCompanies()
    }

    private fun setUpActionbar() {
        (binding.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.str_Companies)
        binding.actionBar.findViewById<View>(R.id.imgBack)
            .setOnClickListener { v: View? -> onBackPressed() }
    }

    private fun setObservers() {
        viewModel.companyResponseLiveData.observe(this) {
            if (it.code == "00") {
                initCompaniesRecyclerview(it.data.companiesList)
            }
        }

        viewModel.dialogLiveData?.observe(this@CompaniesActivity) {
            if (it) {
                Commons.showProgress(this@CompaniesActivity)
            } else {
                Commons.hideProgress()
            }
        }

    }

    private fun manageClicks() {
        binding.btnAddCompany.setOnClickListener {
            startActivity(Intent(this@CompaniesActivity, AddCompanyActivity::class.java))
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

    private fun initCompaniesRecyclerview(busArrayList: ArrayList<CompanyResponse.CompanyData>) {
        adapter = CompanyAdapter(busArrayList, this)
        binding.rvCompanies.setHasFixedSize(true)
        binding.rvCompanies.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(
            binding.rvCompanies.context,
            DividerItemDecoration.VERTICAL
        )
        binding.rvCompanies.addItemDecoration(dividerItemDecoration)
        binding.rvCompanies.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCompanies()
    }
}