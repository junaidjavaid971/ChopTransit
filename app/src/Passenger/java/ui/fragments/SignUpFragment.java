package ui.fragments;

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
import app.com.choptransit.databinding.FragmentSignUpBinding;
import app.com.choptransit.utilities.Commons;
import app.com.choptransit.interfaces.OtpVerifiedCallback;
import app.com.choptransit.viewmodels.AuthViewModel;

public class SignUpFragment extends Fragment {

    FragmentSignUpBinding binding;
    AuthViewModel viewModel;
    OtpVerifiedCallback callback;

    public SignUpFragment() {
    }

    public SignUpFragment(OtpVerifiedCallback callback) {
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sign_up, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        binding.setSignupViewModel(viewModel);
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
                callback.onOtpVerified(viewModel.loginModel.phoneNumber);
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