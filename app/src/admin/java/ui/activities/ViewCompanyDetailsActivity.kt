package ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import app.com.choptransit.R
import app.com.choptransit.databinding.ActivityViewCompanyDetailsBinding
import app.com.choptransit.models.response.CompanyResponse
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.AdminViewModel

class ViewCompanyDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityViewCompanyDetailsBinding
    lateinit var company: CompanyResponse.CompanyData

    lateinit var viewModel: AdminViewModel

    var companyID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_company_details)

        binding = DataBindingUtil.setContentView(
            this@ViewCompanyDetailsActivity,
            R.layout.activity_view_company_details
        )

        viewModel = ViewModelProvider(this).get(AdminViewModel::class.java)

        companyID = intent.getStringExtra("companyID").toString()

        setUpActionbar()
        setupObservers()
        manageClicks()
        viewModel.getCompanyByID(companyID)
    }


    private fun setupObservers() {
        viewModel.companyResponseLiveData.observe(this) {
            if (it.code.equals("00")) {
                company = it.data.companiesList[0]
                setupCompany()
            }
        }
        viewModel.dialogLiveData.observe(this) {
            if (it) {
                Commons.showProgress(this@ViewCompanyDetailsActivity)
            } else {
                Commons.hideProgress()
            }
        }
        viewModel.baseResponseLiveData.observe(this) {
            if (it.code.equals("00")) {
                Commons.showToast(this@ViewCompanyDetailsActivity, it.desc)
                onBackPressed()
            } else {
                Commons.showToast(this@ViewCompanyDetailsActivity, it.desc)
            }
        }
    }


    private fun manageClicks() {
        binding.btnEdit.setOnClickListener {
            startActivity(
                Intent(
                    this@ViewCompanyDetailsActivity,
                    AddCompanyActivity::class.java
                ).putExtra("company", company)
            )
        }

        binding.btnDelete.setOnClickListener {
            Commons.showAlertDialog(
                "Delete Company",
                "Are you sure you want to delete this company? ",
                this
            ) {
                viewModel.deleteCompany(company.id)
            }
        }
    }


    private fun setUpActionbar() {
        (binding.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.str_companyDetails)
        binding.actionBar.findViewById<View>(R.id.imgBack)
            .setOnClickListener { v: View -> onBackPressed() }
    }

    private fun setupCompany() {
        binding.tvCompanyName.text = company.companyName
        binding.tvContactNumber.text = company.companyContactNumber
        binding.tvCompanyEmail.text = company.companyEmail
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCompanyByID(companyID)
    }
}