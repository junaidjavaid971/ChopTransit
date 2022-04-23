package ui.fragments

import adapters.BookingsAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import app.com.choptransit.R
import app.com.choptransit.databinding.FragmentConfirmedTripsBinding
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.DriverViewModel
import ui.activities.TripsActivity

class ConfirmedTripsFragment(val passengerID: String) : Fragment() {
    lateinit var binding: FragmentConfirmedTripsBinding
    lateinit var viewModel: DriverViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_confirmed_trips, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[DriverViewModel::class.java]

        setObservers()
        viewModel.getConfirmedBookingsForPassenger(passengerID)
    }

    private fun setObservers() {
        viewModel.confirmedBookingResponseLiveData.observe(requireActivity()) {
            if (it.code.equals("00")) {
                if (it.data.bookingsList.isEmpty()) {
                    binding.animation.visibility = View.VISIBLE
                    binding.rvConfirmedBookings.visibility = View.GONE
                } else {
                    binding.animation.visibility = View.GONE
                    binding.rvConfirmedBookings.visibility = View.VISIBLE

                    val adapter = BookingsAdapter(it.data.bookingsList, requireActivity(), true)
                    binding.rvConfirmedBookings.setHasFixedSize(true)
                    binding.rvConfirmedBookings.layoutManager =
                        LinearLayoutManager(requireActivity())
                    binding.rvConfirmedBookings.adapter = adapter
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

        viewModel.getConfirmedBookingsForPassenger(passengerID)
    }
}