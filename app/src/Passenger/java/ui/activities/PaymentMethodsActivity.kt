package ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import app.com.choptransit.R
import app.com.choptransit.databinding.ActivityPaymentMethodsBinding
import app.com.choptransit.models.observer.CardObserverModel
import app.com.choptransit.models.response.CardResponse
import app.com.choptransit.models.response.PassengerResponse
import app.com.choptransit.utilities.Commons
import app.com.choptransit.utilities.SharePrefData
import app.com.choptransit.viewmodels.PassengerViewModel
import ui.CardBottomSheetLayout


class PaymentMethodsActivity : AppCompatActivity(), CardBottomSheetLayout.ItemClickListener {
    lateinit var binding: ActivityPaymentMethodsBinding
    lateinit var addPhotoBottomSheetLayout: CardBottomSheetLayout
    lateinit var viewModel: PassengerViewModel
    lateinit var passenger: PassengerResponse.PassengerData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_methods)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[PassengerViewModel::class.java]

        passenger = intent.getSerializableExtra("passenger") as PassengerResponse.PassengerData

        binding.card.flipOnClick = true

        binding.tvAddPaymentMethod.setOnClickListener {
            showBottomSheet()
        }

        binding.tvDelPaymentMethod.setOnClickListener {
            Commons.showAlertDialog(
                "Delete Card",
                "Are you sure you want to delete this card?",
                this
            ) {
                viewModel.deleteCard(passenger.customerID, passenger.cardID, passenger.id)
            }
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        setObservers()

        if (!passenger.cardID.isEmpty()) {
            viewModel.getCardDetails(passenger.customerID, passenger.cardID)
        }
    }

    private fun showBottomSheet() {
        addPhotoBottomSheetLayout = CardBottomSheetLayout.newInstance()
        addPhotoBottomSheetLayout.show(
            supportFragmentManager,
            CardBottomSheetLayout.TAG
        )
    }

    private fun setObservers() {
        viewModel.saveCardLiveData.observe(this) {
            if (it.code.equals("00")) {
                Commons.showToast(this@PaymentMethodsActivity, it.desc)
                viewModel.getPassengerInfo(passenger.id, 6)

                addPhotoBottomSheetLayout.dismiss()
            } else {
                Commons.showToast(this@PaymentMethodsActivity, it.desc)
            }
        }

        viewModel.cardLiveData.observe(this) {
            if (it.code.equals("00")) {
                if (it.data != null) {
                    val cardData: CardResponse.CardData = it.data
                    val expiryMonth =
                        if (cardData.expMonth.toString().length < 2) {
                            "0" + cardData.expMonth.toString()
                        } else {
                            cardData.expMonth.toString()
                        }

                    val cardNumber = "xxxx xxxx xxxx " + cardData.last4
                    binding.card.setCardData(
                        passenger.name,
                        cardNumber,
                        "xxx",
                        expiryMonth + cardData.expYear
                    )

                    binding.tvAddPaymentMethod.visibility = View.GONE
                    binding.tvDelPaymentMethod.visibility = View.VISIBLE
                    binding.card.visibility = View.VISIBLE
                }
            } else {
                Commons.showToast(this@PaymentMethodsActivity, it.desc)
            }
        }

        viewModel.dialogLiveData.observe(this) {
            if (it) {
                Commons.showProgress(this@PaymentMethodsActivity)
            } else {
                Commons.hideProgress()
            }
        }

        viewModel.passengerResponseLiveData.observe(this) {
            passenger = it.data

            SharePrefData.getInstance().deletePrefData(this@PaymentMethodsActivity, "passenger")
            SharePrefData.getInstance()
                .setPassengerData(this@PaymentMethodsActivity, "passenger", passenger)

            viewModel.getCardDetails(passenger.customerID, passenger.cardID)
        }

        viewModel.baseResponseLiveData.observe(this) {
            if (it.code.equals("00")) {
                Commons.showToast(this@PaymentMethodsActivity, it.desc)

                binding.card.visibility = View.GONE
                binding.tvDelPaymentMethod.visibility = View.GONE
                binding.tvAddPaymentMethod.visibility = View.VISIBLE
            } else {
                Commons.showToast(this@PaymentMethodsActivity, it.desc)
            }
        }
    }

    override fun onCancelClicked() {
        addPhotoBottomSheetLayout.dismiss()
    }

    override fun onAddCardClicked(cardObserverModel: CardObserverModel) {
        viewModel.saveCardDetails(cardObserverModel, passenger.id, passenger.email)
    }

}