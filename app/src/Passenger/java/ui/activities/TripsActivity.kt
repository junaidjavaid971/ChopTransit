package ui.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.com.choptransit.R
import app.com.choptransit.databinding.ActivityTripsBinding
import app.com.choptransit.models.response.PassengerResponse
import app.com.choptransit.viewmodels.DriverViewModel
import ui.fragments.ActiveTripsFragment
import ui.fragments.ConfirmedTripsFragment
import ui.fragments.PendingTripsFragment

class TripsActivity : AppCompatActivity() {
    lateinit var binding: ActivityTripsBinding
    lateinit var viewModel: DriverViewModel
    lateinit var passenger: PassengerResponse.PassengerData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_trips)
        viewModel = ViewModelProvider(this)[DriverViewModel::class.java]

        passenger = intent.getSerializableExtra("passenger") as PassengerResponse.PassengerData

        setUpActionbar()

        replaceFragment(PendingTripsFragment(passengerID = passenger.id))
        binding.tvPendingTrips.setOnClickListener {
            binding.tvActiveTrips.background =
                ContextCompat.getDrawable(this, R.drawable.right_cornered_shape_outlined)
            binding.tvConfirmedTrips.background =
                ContextCompat.getDrawable(this, R.drawable.center_shape_outlined)
            binding.tvPendingTrips.background =
                ContextCompat.getDrawable(this, R.drawable.left_cornered_shape)

            binding.tvActiveTrips.setTextColor(ContextCompat.getColor(this, R.color.primaryColor))
            binding.tvConfirmedTrips.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.primaryColor
                )
            )
            binding.tvPendingTrips.setTextColor(ContextCompat.getColor(this, R.color.white))

            replaceFragment(PendingTripsFragment(passenger.id))
        }

        binding.tvConfirmedTrips.setOnClickListener {
            binding.tvActiveTrips.background =
                ContextCompat.getDrawable(this, R.drawable.right_cornered_shape_outlined)
            binding.tvConfirmedTrips.background =
                ContextCompat.getDrawable(this, R.drawable.center_shape)
            binding.tvPendingTrips.background =
                ContextCompat.getDrawable(this, R.drawable.left_cornered_shape_outline)

            binding.tvActiveTrips.setTextColor(ContextCompat.getColor(this, R.color.primaryColor))
            binding.tvConfirmedTrips.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            binding.tvPendingTrips.setTextColor(ContextCompat.getColor(this, R.color.primaryColor))

            replaceFragment(ConfirmedTripsFragment(passenger.id))
        }

        binding.tvActiveTrips.setOnClickListener {
            binding.tvActiveTrips.background =
                ContextCompat.getDrawable(this, R.drawable.right_cornered_shape)
            binding.tvConfirmedTrips.background =
                ContextCompat.getDrawable(this, R.drawable.center_shape_outlined)
            binding.tvPendingTrips.background =
                ContextCompat.getDrawable(this, R.drawable.left_cornered_shape_outline)

            binding.tvActiveTrips.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.tvConfirmedTrips.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.primaryColor
                )
            )
            binding.tvPendingTrips.setTextColor(ContextCompat.getColor(this, R.color.primaryColor))

            replaceFragment(ActiveTripsFragment(passenger.id))
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.frameLayout, fragment)
        ft.commit()
    }

    private fun setUpActionbar() {
        (binding.actionBar.findViewById<View>(R.id.tvTitle) as TextView).text =
            getString(R.string.str_yourTrips)
        binding.actionBar.findViewById<View>(R.id.imgBack)
            .setOnClickListener { v: View? -> onBackPressed() }
    }
}