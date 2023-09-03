package com.tech.foodzilla.presentation.main.fragment.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tech.foodzilla.data.repository.ShopRepository
import com.tech.foodzilla.model.ProductModel
import com.tech.foodzilla.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CartViewModel
@Inject
constructor(
    private val shopRepository: ShopRepository
) : ViewModel() {

    private val _cartProductsLiveData = MutableLiveData<Resource<List<ProductModel>>>()
    val cartProductsLiveData: LiveData<Resource<List<ProductModel>>> = _cartProductsLiveData

    fun deleteProductFromCart(productModel: ProductModel){
        viewModelScope.launch(Dispatchers.IO) {
            shopRepository.deleteProductFromUserCart(productModel.id)
            withContext(Dispatchers.Main){
                getAllCartProducts()
            }
        }
    }

    fun getAllCartProducts() {
        _cartProductsLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            _cartProductsLiveData.postValue(
                shopRepository.getAllUserProducts()
            )
        }
    }

}