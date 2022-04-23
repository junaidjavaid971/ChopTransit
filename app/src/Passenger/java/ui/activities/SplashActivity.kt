package ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import app.com.choptransit.BuildConfig
import app.com.choptransit.R
import app.com.choptransit.databinding.ActivitySplashBinding
import app.com.choptransit.models.response.PassengerResponse
import app.com.choptransit.utilities.SharePrefData
import app.com.choptransit.viewmodels.PassengerViewModel
import com.google.firebase.messaging.FirebaseMessaging

class SplashActivity : AppCompatActivity() {
    lateinit var viewModel: PassengerViewModel
    var passenger: PassengerResponse.PassengerData? = null
    lateinit var binding: ActivitySplashBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        viewModel = ViewModelProvider(this)[PassengerViewModel::class.java]
        passenger = SharePrefData.getInstance().getPassengerPref(this, "passenger")


        binding.tvVersion.text = "Chop Chop Passenger v" + BuildConfig.VERSION_NAME
        if (passenger != null) {
            setObservers()
            getFirebaseToken()
        } else {
            Handler().postDelayed({
                startActivity(
                    Intent(this@SplashActivity, LoginActivity::class.java).addFlags(
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
                Intent(this@SplashActivity, LoginActivity::class.java).addFlags(
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

                        viewModel.savePassengerToken(token, passenger?.userID)
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