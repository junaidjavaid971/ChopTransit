package ui.fragments

import adapters.BookingsAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import app.com.choptransit.R
import app.com.choptransit.databinding.FragmentPendingTripsBinding
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.DriverViewModel
import app.com.choptransit.viewmodels.PassengerViewModel
import ui.activities.TripsActivity

class PendingTripsFragment(val passengerID: String) : Fragment() {

    lateinit var binding: FragmentPendingTripsBinding
    lateinit var viewModel: DriverViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_pending_trips, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[DriverViewModel::class.java]
        setObservers()
        viewModel.getPendingBookingsForPassenger(passengerID)
    }

    private fun setObservers() {
        viewModel.bookingResponseLiveData.observe(requireActivity()) {
            if (it.code.equals("00")) {
                if (it.data.bookingsList.isEmpty()) {
                    binding.animation.visibility = View.VISIBLE
                    binding.rvPendingBookings.visibility = View.GONE
                } else {
                    binding.animation.visibility = View.GONE
                    binding.rvPendingBookings.visibility = View.VISIBLE

                    val adapter = BookingsAdapter(it.data.bookingsList, requireActivity(), false)
                    binding.rvPendingBookings.setHasFixedSize(true)
                    binding.rvPendingBookings.layoutManager = LinearLayoutManager(requireActivity())
                    binding.rvPendingBookings.adapter = adapter
                }
            }
        }
        viewModel.baseResponseLiveData.observe(requireActivity()) {
            if (it.code == "00") {
                Commons.showToast(requireActivity(), it.desc)
            } else {
                Commons.showToast(requireActivity(), it.desc)
            }
        }

        viewModel.dialogLiveData.observe(requireActivity()) { showDialog: Boolean ->
            if (showDialog) {
                Commons.showProgress(activity as TripsActivity?)
            } else {
                Commons.hideProgress()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.getPendingBookingsForPassenger(passengerID)
    }
}