package app.com.choptransit.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import app.com.choptransit.R
import app.com.choptransit.databinding.ActivityLoginSignupBinding
import app.com.choptransit.interfaces.OtpVerifiedCallback
import app.com.choptransit.ui.fragments.DriverLoginFragment
import app.com.choptransit.ui.fragments.DriverRegistrationFragment
import app.com.choptransit.ui.fragments.DriverSignupFragment
import app.com.choptransit.utilities.SharePrefData
import app.com.choptransit.viewmodels.DriverViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

open class LoginSignupActivity : AppCompatActivity(), OtpVerifiedCallback {

    lateinit var binding: ActivityLoginSignupBinding
    lateinit var viewModel: DriverViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView(this@LoginSignupActivity, R.layout.activity_login_signup)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(DriverViewModel::class.java)
        binding.loginActivity = viewModel

        setFragment()
        manageClickListeners()
    }

    private fun setFragment() {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.flLogin, DriverLoginFragment())
        ft.commit()
    }

    private fun manageClickListeners() {
        binding.tlOptions.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        val ft = supportFragmentManager.beginTransaction()
                        ft.replace(R.id.flLogin, DriverLoginFragment())
                        ft.commit()
                    }
                    1 -> {
                        val fragmentTransaction = supportFragmentManager.beginTransaction()
                        fragmentTransaction.replace(
                            R.id.flLogin,
                            DriverSignupFragment(this@LoginSignupActivity)
                        )
                        fragmentTransaction.commit()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    override fun onOtpVerified(phoneNumber: String?) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.flLogin, DriverRegistrationFragment())
        ft.commit()
    }

    override fun onStart() {
        super.onStart()

        val driverData = SharePrefData.getInstance().getDriverData(this, "driver")
        if (driverData != null) {
            val intent = Intent(this, DriverMainActivity::class.java)
            intent.putExtra("driver", driverData)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }
}