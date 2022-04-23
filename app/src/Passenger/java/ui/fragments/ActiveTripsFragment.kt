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
import app.com.choptransit.databinding.FragmentActiveTripsBinding
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.DriverViewModel
import ui.activities.TripsActivity

class ActiveTripsFragment(val passengerID: String) : Fragment() {

    lateinit var binding: FragmentActiveTripsBinding
    lateinit var viewModel: DriverViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_active_trips, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[DriverViewModel::class.java]

        setObservers()
        viewModel.getActiveBookingsForPassenger(passengerID)
    }

    private fun setObservers() {
        viewModel.activeBookingResponseLiveData.observe(requireActivity()) {
            if (it.code.equals("00")) {
                if (it.data.bookingsList.isEmpty()) {
                    binding.animation.visibility = View.VISIBLE
                    binding.rvActiveBookings.visibility = View.GONE
                } else {
                    binding.animation.visibility = View.GONE
                    binding.rvActiveBookings.visibility = View.VISIBLE

                    val adapter = BookingsAdapter(it.data.bookingsList, requireContext(), false)
                    binding.rvActiveBookings.setHasFixedSize(true)
                    binding.rvActiveBookings.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvActiveBookings.adapter = adapter
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
                Commons.showProgress(activity as TripsActivity)
            } else {
                Commons.hideProgress()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.getActiveBookingsForPassenger(passengerID)
    }

}