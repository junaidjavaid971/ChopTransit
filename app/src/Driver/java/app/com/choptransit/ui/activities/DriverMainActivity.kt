package app.com.choptransit.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import app.com.choptransit.R
import app.com.choptransit.databinding.ActivityDriverMainBinding
import app.com.choptransit.models.response.DriverData
import app.com.choptransit.ui.fragments.DriverLoginFragment
import app.com.choptransit.utilities.Commons
import app.com.choptransit.utilities.SharePrefData
import app.com.choptransit.viewmodels.AdminViewModel
import app.com.choptransit.viewmodels.DriverViewModel

class DriverMainActivity : BaseActivity() {

    lateinit var binding: ActivityDriverMainBinding
    lateinit var viewModel: DriverViewModel
    lateinit var driver: DriverData
    var isSignedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver_main)
        viewModel = ViewModelProvider(this)[DriverViewModel::class.java]

        driver = intent.getSerializableExtra("driver") as DriverData

        binding.tvUsername.text = driver.firstName + " " + driver.lastName

        viewModel.checkSignIn(driver.id)

        manageClicks()
        setObservers()
    }

    private fun manageClicks() {
        binding.tvSignin.setOnClickListener {
            if (isSignedIn) {
                viewModel.signOff(driver.id)
            } else {
                viewModel.signIn(driver.id)
            }
        }

        binding.layoutTripDetails.setOnClickListener {
            startActivity(
                Intent(
                    this@DriverMainActivity,
                    AssignedRouteDetailsActivity::class.java
                ).putExtra("driver", driver)
            )
        }
        binding.layoutRideRequests.setOnClickListener {
            startActivity(
                Intent(
                    this@DriverMainActivity,
                    RideRequestsActivity::class.java
                ).putExtra("driver", driver)
            )
        }
        binding.confirmedPassengers.setOnClickListener {
            startActivity(
                Intent(
                    this@DriverMainActivity,
                    PickPassengersActivity::class.java
                ).putExtra("driver", driver)
            )
        }
        binding.layoutRideHistory.setOnClickListener {
            startActivity(
                Intent(
                    this@DriverMainActivity,
                    DriverRideHistoryActivity::class.java
                ).putExtra("driver", driver)
            )
        }
        binding.layoutLogout.setOnClickListener {
            Commons.showAlertDialog("Logout", "Are you sure you want to logout?", this) {
                SharePrefData.getInstance().deletePrefData(this@DriverMainActivity, "driver")
                startActivity(
                    Intent(this@DriverMainActivity, LoginSignupActivity::class.java).addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                finish()
            }
        }
    }

    private fun setObservers() {
        viewModel.checkSignInLiveData.observe(this) {
            isSignedIn = it.desc.equals("1")

            if (isSignedIn) {
                binding.tvSignin.text = getString(R.string.str_signOut)
                binding.confirmedPassengers.visibility = View.VISIBLE
            } else {
                binding.tvSignin.text = getString(R.string.str_signIn)
                binding.confirmedPassengers.visibility = View.GONE
            }
        }

        viewModel.driverProfileLiveData.observe(this) {
            driver = it.data

            SharePrefData.getInstance().deletePrefData(this@DriverMainActivity, "driver")
            SharePrefData.getInstance()
                .setDriverData(this@DriverMainActivity, "driver", it.data)
        }

        viewModel.baseResponseLiveData.observe(this) {
            if (it.code == "00") {
                isSignedIn = it.desc.equals("1")

                if (isSignedIn) {
                    binding.tvSignin.text = getString(R.string.str_signOut)
                    binding.confirmedPassengers.visibility = View.VISIBLE
                } else {
                    binding.tvSignin.text = getString(R.string.str_signIn)
                    binding.confirmedPassengers.visibility = View.GONE
                }
            }
        }

        viewModel.dialogLiveData.observe(this@DriverMainActivity) { showDialog: Boolean ->
            if (showDialog) {
                Commons.showProgress(this@DriverMainActivity)
            } else {
                Commons.hideProgress()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getDriverProfile(driver.id)
    }
}