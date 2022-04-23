package ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.com.choptransit.R;
import app.com.choptransit.databinding.FragmentRegisterPessengerBinding;
import app.com.choptransit.utilities.Commons;
import app.com.choptransit.utilities.SharePrefData;
import app.com.choptransit.viewmodels.AuthViewModel;
import ui.activities.LoginActivity;
import ui.activities.MainActivity;

public class RegisterPessengerFragment extends Fragment {

    String phoneNumber;
    FragmentRegisterPessengerBinding binding;
    AuthViewModel viewModel;

    public RegisterPessengerFragment() {

    }

    public RegisterPessengerFragment(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_register_pessenger, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        binding.setRegisterPessengerFragment(viewModel);

        viewModel.pessengerSignupModel.setPhoneNumber(phoneNumber);

        setObservers();
    }

    private void setObservers() {
        viewModel.passengerResponseLiveData.observe(getActivity(), baseResponse -> {
            if (baseResponse.getCode().equals("00")) {

                SharePrefData.getInstance().setPassengerData(getActivity(), "passenger", baseResponse.getData());
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("passenger", baseResponse.getData());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            } else {
                Commons.showToast(getActivity(), baseResponse.getDesc());
            }
        });
    }
}