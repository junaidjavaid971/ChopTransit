package app.com.choptransit.ui

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import app.com.choptransit.R
import app.com.choptransit.databinding.ActivityDragableMapBinding
import app.com.choptransit.utilities.Commons
import java.io.IOException
import java.util.*
import kotlin.math.ln


class DragableMapActivity : AppCompatActivity(), DragableMapsFragment.MapLocationChangedCallback {
    lateinit var binding: ActivityDragableMapBinding

    var receivedLatitude: Double = 0.0
    var receivedLongitude: Double = 0.0
    var address = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_dragable_map)
        setContentView(binding.root)

        receivedLatitude = intent.getDoubleExtra("latitude", 0.0)
        receivedLongitude = intent.getDoubleExtra("longitude", 0.0)

        Commons.showProgress(this)

        setFragment()

        binding.btnPickLocation.setOnClickListener {
            sendIntentResponse()
        }
    }


    private fun setFragment() {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(
            R.id.frameLayout,
            DragableMapsFragment(this, receivedLatitude, receivedLongitude)
        )
        ft.commit()
    }

    private fun getAddress(lat: Double, lng: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(lat, lng, 1)
            val obj: Address = addresses[0]
            var add: String = obj.getAddressLine(0)
            add = """
            $add
            ${obj.getCountryName()}
            """.trimIndent()
            add = """
            $add
            ${obj.getCountryCode()}
            """.trimIndent()
            add = """
            $add
            ${obj.getAdminArea()}
            """.trimIndent()
            add = """
            $add
            ${obj.getPostalCode()}
            """.trimIndent()
            add = """
            $add
            ${obj.getSubAdminArea()}
            """.trimIndent()
            add = """
            $add
            ${obj.getLocality()}
            """.trimIndent()
            add = """
            $add
            ${obj.getSubThoroughfare()}
            """.trimIndent()

            address = add.trim().split("\n")[0]

            receivedLongitude = lng
            receivedLatitude = lat

            Log.v("IGA", "Address$add")

            binding.etDropOffLocation.setText(address)

            Commons.hideProgress()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapLocationChangedCallback(latitude: Double, longitude: Double) {
        getAddress(latitude, longitude)
    }

    override fun onBackPressed() {
        sendIntentResponse()
    }

    private fun sendIntentResponse() {
        val intent = Intent()

        intent.putExtra("latitude", receivedLatitude)
        intent.putExtra("latitude", receivedLongitude)
        intent.putExtra("address", address)
        setResult(RESULT_OK, intent)
        finish()
    }
}