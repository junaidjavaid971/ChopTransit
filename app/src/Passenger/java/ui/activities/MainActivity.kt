package ui.activities

import adapters.AssignedRoutesAdapter
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.com.choptransit.R
import app.com.choptransit.databinding.ActivityPassengerMainBinding
import app.com.choptransit.models.response.AssignRouteData
import app.com.choptransit.models.response.PassengerResponse
import app.com.choptransit.utilities.Commons
import app.com.choptransit.utilities.SharePrefData
import app.com.choptransit.viewmodels.AdminViewModel
import app.com.choptransit.viewmodels.PassengerViewModel
import ui.fragments.MapsFragment


open class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE: Int = 321545
    lateinit var binding: ActivityPassengerMainBinding

    lateinit var passenger: PassengerResponse.PassengerData
    lateinit var viewModel: AdminViewModel
    lateinit var passengerViewModel: PassengerViewModel
    lateinit var mapsFragment: MapsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_passenger_main)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[AdminViewModel::class.java]
        passengerViewModel = ViewModelProvider(this)[PassengerViewModel::class.java]
        passenger = intent.getSerializableExtra("passenger") as PassengerResponse.PassengerData
        mapsFragment = MapsFragment()

        checkPermission()

        setFragment()
        manageClicks()
        setObservers()

        viewModel.getAssignedRoutes()
        viewModel.getRoutes()
    }

    private fun setObservers() {
        viewModel.assignRouteReponseLiveData.observe(this) {
            if (it.code.equals("00")) {
                val arrayList = it.data
                initRoutesRecyclerview(arrayList)
            }
        }

        passengerViewModel.passengerResponseLiveData.observe(this) {
            passenger = it.data

            SharePrefData.getInstance().deletePrefData(this@MainActivity, "passenger")
            SharePrefData.getInstance()
                .setPassengerData(this@MainActivity, "passenger", passenger)
        }
    }

    private fun manageClicks() {
        binding.bottomSheet.findViewById<EditText>(R.id.etDropOffLocation).setOnClickListener {
            startActivity(Intent(this@MainActivity, SearchRouteActivity::class.java))
        }

        binding.ivMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(Gravity.LEFT)
        }

        binding.navLayout.findViewById<TextView>(R.id.tvYourTrips).setOnClickListener {
            binding.drawerLayout.closeDrawer(Gravity.LEFT)
            startActivity(
                Intent(this@MainActivity, TripsActivity::class.java).putExtra(
                    "passenger",
                    passenger
                )
            )
            binding.drawerLayout.closeDrawer(Gravity.LEFT)
        }
        binding.navLayout.findViewById<TextView>(R.id.tvPayment).setOnClickListener {
            binding.drawerLayout.closeDrawer(Gravity.LEFT)
            startActivity(
                Intent(this@MainActivity, PaymentMethodsActivity::class.java).putExtra(
                    "passenger",
                    passenger
                )
            )
        }
        binding.navLayout.findViewById<TextView>(R.id.tvRidesHistory).setOnClickListener {
            binding.drawerLayout.closeDrawer(Gravity.LEFT)
            startActivity(
                Intent(this@MainActivity, RideHistoryActivity::class.java).putExtra(
                    "passenger",
                    passenger
                )
            )
        }
        binding.navLayout.findViewById<TextView>(R.id.tvLogout).setOnClickListener {
            binding.drawerLayout.closeDrawer(Gravity.LEFT)
            Commons.showAlertDialog("Logout", "Are you sure you want to logout?", this) {
                SharePrefData.getInstance().deletePrefData(this@MainActivity, "passenger")
                startActivity(
                    Intent(this@MainActivity, LoginActivity::class.java).addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                finish()
            }
        }
    }

    private fun setFragment() {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.flMap, mapsFragment)
        ft.commit()
    }

    private fun initRoutesRecyclerview(routeArrayList: ArrayList<AssignRouteData>) {
        val adapter = AssignedRoutesAdapter(routeArrayList, this@MainActivity)
        val rTrips = binding.bottomSheet.findViewById<RecyclerView>(R.id.rvRecentTrips)
        rTrips.setHasFixedSize(true)
        rTrips.layoutManager = LinearLayoutManager(this)
        rTrips.adapter = adapter

    }

    override fun onResume() {
        super.onResume()
        passengerViewModel.getPassengerInfo(passenger.id, 6)
        viewModel.getAssignedRoutes()
    }

    open fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            mapsFragment.getCurrentLocation()
        }
    }

}