package ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;

import app.com.choptransit.R;
import app.com.choptransit.databinding.ActivityLoginBinding;
import app.com.choptransit.models.response.PassengerResponse;
import app.com.choptransit.ui.DragableMapActivity;
import app.com.choptransit.utilities.SharePrefData;
import ui.fragments.LoginFragment;
import ui.fragments.RegisterPessengerFragment;
import ui.fragments.SignUpFragment;
import app.com.choptransit.interfaces.OtpVerifiedCallback;
import app.com.choptransit.viewmodels.AuthViewModel;

public class LoginActivity extends AppCompatActivity implements OtpVerifiedCallback {

    ActivityLoginBinding binding;
    AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        binding.setLoginActivity(authViewModel);
        setContentView(binding.getRoot());
        setFragment();
        manageClickListeners();
    }

    private void manageClickListeners() {
        binding.tlOptions.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.flLogin, new LoginFragment());
                        ft.commit();
                        break;
                    case 1:
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.flLogin, new SignUpFragment(LoginActivity.this));
                        fragmentTransaction.commit();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    protected void setFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flLogin, new LoginFragment());
        ft.commit();
    }

    @Override
    public void onOtpVerified(String phoneNumber) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flLogin, new RegisterPessengerFragment(phoneNumber));
        ft.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        PassengerResponse.PassengerData passengerData = SharePrefData.getInstance().getPassengerPref(this, "passenger");
        if (passengerData != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("passenger", passengerData);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}