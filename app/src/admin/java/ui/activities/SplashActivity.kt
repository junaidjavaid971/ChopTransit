package ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.databinding.DataBindingUtil
import app.com.choptransit.BuildConfig
import app.com.choptransit.R
import app.com.choptransit.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        binding.tvVersion.text = "Chop Chop Admin v" + BuildConfig.VERSION_NAME

        Handler().postDelayed({
            startActivity(
                Intent(this@SplashActivity, AdminDashboardActivity::class.java).addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            finish()
        }, 2000)
    }
}