package ui;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;

import app.com.choptransit.R;
import app.com.choptransit.databinding.AddCardBottomSheetBinding;
import app.com.choptransit.models.observer.CardObserverModel;
import app.com.choptransit.viewmodels.PassengerViewModel;
import utils.CreditCardNumberTextWatcher;

public class CardBottomSheetLayout extends BottomSheetDialogFragment
        implements View.OnClickListener {

    public static final String TAG = "ActionBottomDialog";
    private ItemClickListener mListener;
    AddCardBottomSheetBinding binding;
    PassengerViewModel viewModel;
    int count = 0;

    public static CardBottomSheetLayout newInstance() {
        return new CardBottomSheetLayout();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogStyle);
        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.add_card_bottom_sheet, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PassengerViewModel.class);
        binding.setPaymentSheetLayout(viewModel);

        binding.ivCancel.setOnClickListener(this);
        binding.btnAddCard.setOnClickListener(this);

        setObservers();
        setTextWatchers();
    }

    private void setTextWatchers() {
        binding.etCardHolderName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.tvError.setVisibility(View.GONE);
            }
        });

        TextWatcher textWatcher = new CreditCardNumberTextWatcher(binding.etCardNumber, binding.tvError);
        binding.etCardNumber.addTextChangedListener(textWatcher);

        binding.etExpiry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.tvError.setVisibility(View.GONE);

                if (editable.length() > 0 && (editable.length() % 3) == 0) {
                    final char c = editable.charAt(editable.length() - 1);
                    if ('/' == c) {
                        editable.delete(editable.length() - 1, editable.length());
                    }
                }
                if (editable.length() > 0 && (editable.length() % 3) == 0) {
                    char c = editable.charAt(editable.length() - 1);
                    if (Character.isDigit(c) && TextUtils.split(editable.toString(), String.valueOf("/")).length <= 2) {
                        editable.insert(editable.length() - 1, String.valueOf("/"));
                    }
                }
                viewModel.cardObserverModel.setExpiry(editable.toString());

                if (editable.toString().length() == 5) {
                    String[] expiryDates = viewModel.cardObserverModel.getExpiry().split("/");
                    if (Integer.parseInt(expiryDates[1]) < Calendar.getInstance().get(Calendar.YEAR) % 100) {
                        binding.tvError.setText(getString(R.string.str_expiryDateRequired));
                        binding.tvError.setVisibility(View.VISIBLE);
                    }

                    if (Integer.parseInt(expiryDates[0]) < Calendar.getInstance().get(Calendar.MONTH) + 1 &&
                            Integer.parseInt(expiryDates[1]) == Calendar.getInstance().get(Calendar.YEAR) % 100) {
                        binding.tvError.setText(getString(R.string.str_expiryDateRequired));
                        binding.tvError.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        binding.etCVC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.tvError.setVisibility(View.GONE);
            }
        });
    }

    private void setObservers() {
        viewModel.errorLiveData.observe(this, integer -> {
            switch (integer) {
                case R.id.etCardHolderName:
                    binding.tvError.setText(R.string.str_cardHolderNameRequired);
                    binding.tvError.setVisibility(View.VISIBLE);
                    break;
                case R.id.etCardNumber:
                    binding.tvError.setText(R.string.str_cardNumberRequired);
                    binding.tvError.setVisibility(View.VISIBLE);
                    break;
                case R.id.etExpiry:
                    binding.tvError.setText(R.string.str_expiryDateRequired);
                    binding.tvError.setVisibility(View.VISIBLE);
                    break;
                case R.id.etCVC:
                    binding.tvError.setText(R.string.str_CVCRequired);
                    binding.tvError.setVisibility(View.VISIBLE);
                    break;
                case -1:
                    mListener.onAddCardClicked(viewModel.cardObserverModel);
                    break;
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ItemClickListener) {
            mListener = (ItemClickListener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement ItemClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.ivCancel.getId()) {
            mListener.onCancelClicked();
        } else if (view.getId() == binding.btnAddCard.getId()) {
            binding.tvError.setVisibility(View.GONE);
            viewModel.validateCardInformation();
        }
    }

    public interface ItemClickListener {
        void onCancelClicked();

        void onAddCardClicked(CardObserverModel cardObserverModel);
    }
}