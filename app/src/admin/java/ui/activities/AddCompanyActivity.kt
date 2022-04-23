package ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import app.com.choptransit.R
import app.com.choptransit.databinding.ActivityAddCompanyBinding
import app.com.choptransit.models.response.CompanyResponse
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.AdminViewModel

class AddCompanyActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddCompanyBinding
    lateinit var viewModel: AdminViewModel

    lateinit var company: CompanyResponse.CompanyData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_company)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AdminViewModel::class.java)
        binding.addCompanyViewModel = viewModel

        if (intent.hasExtra("company")) {
            company = intent.getSerializableExtra("company") as CompanyResponse.CompanyData
            setupCompanyForm(company)
        }
        setObservers()
    }

    private fun setupCompanyForm(company: CompanyResponse.CompanyData) {
        binding.btnAddBus.text = getString(R.string.str_updateCompany)
        (binding.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.str_updateCompany)
        viewModel.companyModel.id = company.id
        viewModel.companyModel.companyName = company.companyName
        viewModel.companyModel.companyContactNumber = company.companyContactNumber
        viewModel.companyModel.companyEmail = company.companyEmail
        viewModel.companyModel.updateCompany = true
    }


    private fun setObservers() {
        viewModel.baseResponseLiveData?.observe(this@AddCompanyActivity) {
            if (it.code == "00") {
                Commons.showToast(this@AddCompanyActivity, it.desc)
                onBackPressed()
            } else {
                Commons.showToast(this@AddCompanyActivity, it.desc)
            }
        }

        viewModel.dialogLiveData?.observe(this@AddCompanyActivity) {
            if (it) {
                Commons.showProgress(this@AddCompanyActivity)
            } else {
                Commons.hideProgress()
            }
        }

        viewModel.errorLiveData?.observe(this@AddCompanyActivity) {
            Commons.showToast(this@AddCompanyActivity, it)
        }
    }
}