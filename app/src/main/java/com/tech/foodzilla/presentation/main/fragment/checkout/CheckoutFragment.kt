package com.tech.foodzilla.presentation.main.fragment.checkout

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tech.foodzilla.R
import com.tech.foodzilla.databinding.FragmentCheckoutBinding
import com.tech.foodzilla.model.CheckoutModel
import com.tech.foodzilla.model.OrderModel
import com.tech.foodzilla.model.PaymentModel
import com.tech.foodzilla.presentation.authentication.UserInfoViewModel
import com.tech.foodzilla.util.DISPLAY_DIALOG
import com.tech.foodzilla.util.LOADING_ANNOTATION
import com.tech.foodzilla.util.Resource
import com.tech.foodzilla.util.extention.closeFragment
import com.tech.foodzilla.util.extention.hide
import com.tech.foodzilla.util.extention.show
import com.tech.foodzilla.util.extention.showToast
import com.stripe.android.PaymentConfiguration
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.payments.paymentlauncher.PaymentLauncher
import com.stripe.android.payments.paymentlauncher.PaymentResult
import com.stripe.android.view.CardMultilineWidget
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named


@AndroidEntryPoint
class CheckoutFragment : BottomSheetDialogFragment() {

    private val userInfoViewModel by activityViewModels<UserInfoViewModel>()
    private val checkoutViewModel by activityViewModels<CheckoutViewModel>()

    private lateinit var binding: FragmentCheckoutBinding
    private val args by navArgs<CheckoutFragmentArgs>()
    private val totalCost by lazy { args.totalCost }
    private val cartProductsList by lazy { args.productList }
    private lateinit var orderModel: OrderModel
    private var userLocation = ""
    private lateinit var paymentLauncher: PaymentLauncher
    private lateinit var paymentIntentClientSecret: String

    @Inject
    @Named(DISPLAY_DIALOG)
    lateinit var displayAlert: AlertDialog

    @Inject
    @Named(LOADING_ANNOTATION)
    lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_checkout, container, false)
        return binding.run {
            fragment = this@CheckoutFragment
            loadData = false
            binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeListener()
        initPaymentLauncher()
        startCheckout()
    }

    private fun observeListener() {
        // gt user information from firebase to get user location and save in userLocation object to pass with user order .
        userInfoViewModel.userInformationLiveData.observe(viewLifecycleOwner, { userInfo ->
            when (userInfo) {
                is Resource.Success -> {
                    userLocation = userInfo.data?.userLocationName!!
                    val checkoutModel =
                        CheckoutModel(userLocation, totalCost, getString(R.string.payment))
                    binding.checkoutModel = checkoutModel
                }
                is Resource.Error -> {
                    showToast(userInfo.msg!!)
                }
            }
        })

        // observe to get payment intent client secret to start payment process.
        checkoutViewModel.paymentIntentClientSecretLiveData.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    paymentIntentClientSecret = it.data!!
                    binding.loadData = false
                }
                is Resource.Error -> {
                    showAlertDialog(getString(R.string.error), getString(R.string.paymentError))
                    closeFragment()
                }
                is Resource.Loading -> {
                    binding.loadData = true
                }
            }
        })
        // observe if payment process successfully after order uploaded and the money sent successfully.
        checkoutViewModel.orderProductsLiveData.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    orderModel = it.data!!
                    loadingDialog.hide()
                    navigateToOrderStatusFragment(true)
                }
                is Resource.Error -> {
                    loadingDialog.hide()
                    navigateToOrderStatusFragment(false)
                }
                is Resource.Loading -> loadingDialog.show()
            }
        })
    }

    // create payment configuration to create a thread to start payment process.
    private fun initPaymentLauncher() {
        val paymentConfiguration =
            PaymentConfiguration.getInstance(requireContext().applicationContext)
        paymentLauncher = PaymentLauncher.Companion.create(
            this,
            paymentConfiguration.publishableKey,
            paymentConfiguration.stripeAccountId,
            ::onPaymentResult
        )
    }

    // check if visa card have enough money to start the process.
    private fun startCheckout() {
        val amount = (totalCost * 100).toInt().toString()
        PaymentModel(amount).let {
            checkoutViewModel.createPaymentIntent(it)
        }
    }

    // animate when sliding payment method in ui.
    fun selectPaymentMethod(paymentSlider: ImageView, cardInputWidget: CardMultilineWidget) {
        if (cardInputWidget.isShown) {
            paymentSlider.animate().rotationBy(-90f).start()
            cardInputWidget.animate().scaleY(0f).withEndAction {
                cardInputWidget.hide()
            }.start()
        } else {
            paymentSlider.animate().rotationBy(90f).start()
            cardInputWidget.animate().scaleY(1f).withEndAction {
                cardInputWidget.show()
            }.start()
        }
    }

    // add payment listener to check if payment process is completed successfully or not .
    private fun onPaymentResult(paymentResult: PaymentResult) {
        val isOrderSubmitted = when (paymentResult) {
            is PaymentResult.Completed -> true
            else -> false

        }
        binding.loadData = false
        if (isOrderSubmitted) {
            checkoutViewModel.pushUserOrder(cartProductsList, userLocation, totalCost)
        } else {
            navigateToOrderStatusFragment(isOrderSubmitted)
        }
    }

    private fun navigateToOrderStatusFragment(isOrderSubmitted: Boolean) {
        val action =
            CheckoutFragmentDirections.actionCheckoutFragmentToOrderStatusFragment(orderModel, isOrderSubmitted)
        findNavController().navigate(action)
    }

    fun orderNow(cardInputWidget: CardMultilineWidget) {
        // check if payment card widget is hidden and not validate to show a message to user to complete payment process.
        if (!cardInputWidget.isShown && !cardInputWidget.validateCardNumber()) {
            showToast(getString(R.string.addPaymentMethod))
            return
        }
        // start payment process with the previous cost by the products selected on cart fragment.
        cardInputWidget.paymentMethodCreateParams?.let { params ->
            val confirmParams = ConfirmPaymentIntentParams
                .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret)
            paymentLauncher.confirm(confirmParams)
            binding.loadData = true
        }
    }

    private fun showAlertDialog(title: String, message: String) {
        displayAlert.apply {
            setTitle(title)
            setMessage(message)
            show()
        }
    }

    fun closeDialog() {
        closeFragment()
    }
}