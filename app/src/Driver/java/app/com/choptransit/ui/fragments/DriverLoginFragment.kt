package app.com.choptransit.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.com.choptransit.R
import app.com.choptransit.databinding.FragmentDriverLoginBinding
import app.com.choptransit.models.response.DriverRegistrationResponse
import app.com.choptransit.models.response.SendOtpResponse
import app.com.choptransit.ui.activities.DriverMainActivity
import app.com.choptransit.utilities.Commons
import app.com.choptransit.utilities.SharePrefData
import app.com.choptransit.viewmodels.AuthViewModel

class DriverLoginFragment : Fragment() {

    lateinit var binding: FragmentDriverLoginBinding
    lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_driver_login, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        binding.driverLoginFragment = viewModel

        binding.ccp.registerCarrierNumberEditText(binding.edMobileNumber)

        setObservers()
    }


    private fun setObservers() {
        binding.edMobileNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                viewModel.loginModel.setPhoneNumber(binding.ccp.fullNumberWithPlus)
            }
        })

        viewModel.errorLiveData.observe(requireActivity()) { s: String? ->
            Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
        }
        viewModel.sendOtpResponseLiveData.observe(requireActivity()) { loginResponse: SendOtpResponse ->
            if (loginResponse.code == "00") {
                binding.btnOTP.visibility = View.GONE
                binding.verifyOtp.visibility = View.VISIBLE
                binding.tiOTP.visibility = View.VISIBLE
                Commons.showToast(activity, loginResponse.data.toString())
            } else {
                Commons.showToast(activity, loginResponse.desc)
            }
        }
        viewModel.verifyDriverOtpLiveData.observe(requireActivity()) { loginResponse: DriverRegistrationResponse ->
            if (loginResponse.code == "00") {
                val driver = loginResponse.data
                if (loginResponse.data == null) {
                    Commons.showToast(
                        requireActivity(),
                        "You do not have an account with us. Please sign up first!"
                    )
                } else {
                    if (driver.isApproved.equals("0")) {
                        Commons.showToast(
                            requireActivity(),
                            "Your profile is not approved by the admin yet. Please try again later!"
                        )
                    } else {
                        SharePrefData.getInstance()
                            .setDriverData(activity, "driver", loginResponse.data)
                        val intent = Intent(activity, DriverMainActivity::class.java)
                        intent.putExtra("driver", loginResponse.data)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                }
            } else {
                Commons.showToast(requireActivity(), loginResponse.desc)
            }
        }
        viewModel.dialogLiveData.observe(requireActivity()) { showDialog: Boolean ->
            if (showDialog) {
                Commons.showProgress(requireActivity() as AppCompatActivity?)
            } else {
                Commons.hideProgress()
            }
        }
    }
}