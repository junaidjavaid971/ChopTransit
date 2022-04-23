package ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import app.com.choptransit.R;
import app.com.choptransit.databinding.FragmentLoginBinding;
import app.com.choptransit.utilities.Commons;
import app.com.choptransit.utilities.SharePrefData;
import app.com.choptransit.viewmodels.AuthViewModel;
import ui.activities.MainActivity;

public class LoginFragment extends Fragment {

    app.com.choptransit.databinding.FragmentLoginBinding binding;
    AuthViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_login, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        binding.setAuthViewModel(viewModel);

        binding.ccp.registerCarrierNumberEditText(binding.edMobileNumber);

        setObservers();
    }

    private void setObservers() {
        binding.edMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.loginModel.setPhoneNumber(binding.ccp.getFullNumberWithPlus());
            }
        });
        viewModel.errorLiveData.observe(getActivity(), s -> {
            Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
        });

        viewModel.sendOtpResponseLiveData.observe(getActivity(), loginResponse -> {
            if (loginResponse.getCode().equals("00")) {
                binding.btnOTP.setVisibility(View.GONE);
                binding.verifyOtp.setVisibility(View.VISIBLE);
                binding.tiOTP.setVisibility(View.VISIBLE);
                Commons.showToast(getActivity(), String.valueOf(loginResponse.getData()));
            } else {
                Commons.showToast(getActivity(), loginResponse.getDesc());
            }
        });

        viewModel.verifyOtpLiveData.observe(getActivity(), loginResponse -> {
            if (loginResponse.getCode().equals("00")) {
                if (loginResponse.getData() == null) {
                    Commons.showToast(requireActivity(), "You do not have an account with us. Please sign up first!");
                } else {
                    SharePrefData.getInstance().setPassengerData(getActivity(), "passenger", loginResponse.getData());
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("passenger", loginResponse.getData());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }
            } else {
                Commons.showToast(getActivity(), loginResponse.getDesc());
            }
        });

        viewModel.dialogLiveData.observe(getActivity(), showDialog -> {
            if (showDialog) {
                Commons.showProgress((AppCompatActivity) getActivity());
            } else {
                Commons.hideProgress();
            }
        });
    }
}