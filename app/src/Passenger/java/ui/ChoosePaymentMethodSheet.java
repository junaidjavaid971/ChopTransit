package ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import app.com.choptransit.R;
import app.com.choptransit.databinding.PaymentMethodDialogBinding;
import app.com.choptransit.viewmodels.PassengerViewModel;


public class ChoosePaymentMethodSheet extends BottomSheetDialogFragment
        implements View.OnClickListener {

    public static final String TAG = "ActionBottomDialog";
    private ItemClickListener mListener;
    PaymentMethodDialogBinding binding;
    PassengerViewModel viewModel;
    boolean isCardSelected = true;

    public static ChoosePaymentMethodSheet newInstance() {
        return new ChoosePaymentMethodSheet();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.payment_method_dialog, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PassengerViewModel.class);
        binding.setChoosePaymentMethod(viewModel);

        binding.layoutAlipay.setOnClickListener(this);
        binding.layoutCard.setOnClickListener(this);
        binding.btnPay.setOnClickListener(this);
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
        if (view.getId() == binding.layoutAlipay.getId()) {
            binding.layoutAlipay.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_payment_method_selected));
            binding.layoutCard.setBackground(null);

            binding.ivAliPaySelected.setVisibility(View.VISIBLE);
            binding.ivCardSelected.setVisibility(View.GONE);

            isCardSelected = false;
        } else if (view.getId() == binding.layoutCard.getId()) {
            binding.layoutCard.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_payment_method_selected));
            binding.layoutAlipay.setBackground(null);

            binding.ivCardSelected.setVisibility(View.VISIBLE);
            binding.ivAliPaySelected.setVisibility(View.GONE);

            isCardSelected = true;
        } else if (view.getId() == binding.btnPay.getId()) {
            mListener.onPaySelected(isCardSelected);
        }
    }

    public interface ItemClickListener {
        void onPaySelected(boolean isCard);
    }
}