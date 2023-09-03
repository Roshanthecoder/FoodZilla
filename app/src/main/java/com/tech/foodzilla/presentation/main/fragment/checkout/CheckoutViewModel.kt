package com.tech.foodzilla.presentation.main.fragment.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tech.foodzilla.data.repository.ShopRepository
import com.tech.foodzilla.model.OrderModel
import com.tech.foodzilla.model.PaymentModel
import com.tech.foodzilla.model.ProductModel
import com.tech.foodzilla.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel
@Inject
constructor(
    private val shopRepository: ShopRepository
) : ViewModel() {

    private val _paymentIntentClientSecret = MutableLiveData<Resource<String>>()
    val paymentIntentClientSecretLiveData: LiveData<Resource<String>> = _paymentIntentClientSecret

    private val _orderProductsLiveData = MutableLiveData<Resource<OrderModel>>()
    val orderProductsLiveData: LiveData<Resource<OrderModel>> = _orderProductsLiveData

    fun createPaymentIntent(
        paymentModel: PaymentModel
    ) {
        _paymentIntentClientSecret.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            _paymentIntentClientSecret.postValue(shopRepository.createPaymentIntent(paymentModel))
        }
    }

    fun pushUserOrder(cartProductsList: Array<ProductModel>, userLocation: String, totalCost: Float) {
        _orderProductsLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            _orderProductsLiveData.postValue(
                shopRepository.uploadProductsToOrders(cartProductsList, userLocation, totalCost)
            )
        }
    }
}