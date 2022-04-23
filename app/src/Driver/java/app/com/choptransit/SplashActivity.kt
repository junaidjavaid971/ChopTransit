package app.com.choptransit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import app.com.choptransit.databinding.ActivitySplashBinding
import app.com.choptransit.models.response.DriverData
import app.com.choptransit.models.response.PassengerResponse
import app.com.choptransit.ui.activities.LoginSignupActivity
import app.com.choptransit.utilities.SharePrefData
import app.com.choptransit.viewmodels.PassengerViewModel
import com.google.firebase.messaging.FirebaseMessaging

class SplashActivity : AppCompatActivity() {
    lateinit var viewModel: PassengerViewModel
    var driver: DriverData? = null
    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        viewModel = ViewModelProvider(this)[PassengerViewModel::class.java]

        driver = SharePrefData.getInstance().getDriverData(this, "driver")

        binding.tvVersion.text = "Chop Chop Driver v" + BuildConfig.VERSION_NAME

        if (driver != null) {
            setObservers()
            getFirebaseToken()
        } else {
            Handler().postDelayed({
                startActivity(
                    Intent(this@SplashActivity, LoginSignupActivity::class.java).addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                finish()
            }, 2000)
        }
    }

    private fun setObservers() {
        viewModel.baseResponseLiveData.observe(this) {
            if (it.code.equals("00")) {
                switchActivity()
            }
        }
    }

    private fun switchActivity() {
        Handler().postDelayed({
            startActivity(
                Intent(this@SplashActivity, LoginSignupActivity::class.java).addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            finish()
        }, 2000)
    }


    private fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null && !TextUtils.isEmpty(task.result)) {
                        val token: String = task.result!!
                        Log.d("TOKEN", token)

                        viewModel.savePassengerToken(token, driver?.userID)
                    } else {
                        switchActivity()
                    }
                } else {
                    switchActivity()
                }
            }
            .addOnFailureListener {
                switchActivity()
            }
    }
}