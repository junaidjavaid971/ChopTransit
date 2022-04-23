package app.com.choptransit.ui.activities

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
import app.com.choptransit.adapters.SelectCompanyAdapter
import app.com.choptransit.databinding.ActivitySelectCompanyBinding
import app.com.choptransit.models.response.CompanyResponse
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.AdminViewModel

class SelectCompanyActivity : AppCompatActivity(), SelectCompanyAdapter.CompanySelectedCallback {

    lateinit var binding: ActivitySelectCompanyBinding
    lateinit var viewModel: AdminViewModel
    lateinit var adapter: SelectCompanyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this@SelectCompanyActivity,
            R.layout.activity_select_company
        )

        viewModel = ViewModelProvider(this).get(AdminViewModel::class.java)

        viewModel.getCompanies()
        setUpActionbar()
        setObservers()
        manageClicks()
    }

    private fun setUpActionbar() {
        (binding.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.str_Companies)
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
        viewModel.companyResponseLiveData.observe(this) {
            initCompaniesRecyclerview(it.data.companiesList)
        }

        viewModel.dialogLiveData?.observe(this@SelectCompanyActivity) {
            if (it) {
                Commons.showProgress(this@SelectCompanyActivity)
            } else {
                Commons.hideProgress()
            }
        }

    }

    private fun initCompaniesRecyclerview(companiesArrayList: ArrayList<CompanyResponse.CompanyData>) {
        adapter = SelectCompanyAdapter(companiesArrayList, this, this@SelectCompanyActivity)
        binding.rvCompanies.setHasFixedSize(true)
        binding.rvCompanies.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(
            binding.rvCompanies.context,
            DividerItemDecoration.VERTICAL
        )
        binding.rvCompanies.addItemDecoration(dividerItemDecoration)
        binding.rvCompanies.adapter = adapter
    }

    override fun onCompanySelected(company: CompanyResponse.CompanyData) {
        val intent = Intent()
        intent.putExtra("company", company)
        setResult(3121, intent)
        finish()
    }
}