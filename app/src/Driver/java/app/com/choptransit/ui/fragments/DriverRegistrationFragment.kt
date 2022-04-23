package app.com.choptransit.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import app.com.choptransit.R
import app.com.choptransit.databinding.FragmentDriverLoginBinding
import app.com.choptransit.databinding.FragmentDriverRegistrationBinding
import app.com.choptransit.models.response.CompanyResponse
import app.com.choptransit.ui.activities.LoginSignupActivity
import app.com.choptransit.ui.activities.SelectCompanyActivity
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.AuthViewModel
import app.com.choptransit.viewmodels.DriverViewModel
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.*

class DriverRegistrationFragment : Fragment() {

    lateinit var binding: FragmentDriverRegistrationBinding
    lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_driver_registration, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        binding.driverRegistrationFragment = viewModel

        binding.ccp.registerCarrierNumberEditText(binding.edMobileNumber)

        setObservers()
        manageClicks()
    }

    private fun setObservers() {
        binding.edMobileNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                viewModel.driverRegistrationModel.phoneNumber = binding.ccp.fullNumberWithPlus
            }
        })

        viewModel.errorLiveData.observe(requireActivity()) {
            Commons.showToast(requireActivity(), it)
        }

        viewModel.dialogLiveData?.observe(requireActivity()) {
            if (it) {
                Commons.showProgress(activity as LoginSignupActivity?)
            } else {
                Commons.hideProgress()
            }
        }

        viewModel.driverResponseLiveData?.observe(requireActivity()) {
            if (it.code == "00") {
                Commons.showToast(activity as LoginSignupActivity?, it.desc)
                startActivity(
                    Intent(
                        activity,
                        LoginSignupActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
                requireActivity().finish()
            } else {
                Commons.showToast(activity as LoginSignupActivity?, it.desc)
            }
        }
    }

    private fun manageClicks() {
        binding.edCompany.setOnClickListener {
            val intent = Intent(activity, SelectCompanyActivity::class.java)
            startActivityForResult(intent, 3121)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 3121) {
            if (data != null) {
                val companyData: CompanyResponse.CompanyData =
                    data.getSerializableExtra("company") as CompanyResponse.CompanyData

                Commons.showToast(activity, companyData.companyName)
                viewModel.driverRegistrationModel.companyID = companyData.id
                binding.edCompany.setText(companyData.companyName)
            }
        }
    }
}