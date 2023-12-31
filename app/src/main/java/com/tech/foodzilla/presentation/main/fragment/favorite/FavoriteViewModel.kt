package com.tech.foodzilla.presentation.main.fragment.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tech.foodzilla.util.Resource
import com.tech.foodzilla.data.repository.ShopRepository
import com.tech.foodzilla.model.ProductModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel
@Inject
constructor(
    private val shopRepository: ShopRepository
) : ViewModel() {

    private lateinit var _favoriteProductsLiveData: LiveData<List<ProductModel>>
    val favoriteProductsLiveData get() = _favoriteProductsLiveData

    private val _cartProductsLiveData = MutableLiveData<Resource<Any>>()
    val cartProductsLiveData: LiveData<Resource<Any>> = _cartProductsLiveData


    fun getFavoriteProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            _favoriteProductsLiveData = shopRepository.getFavoriteProductsLiveData()
        }
    }

    fun addProductsToCart(favList: List<ProductModel>) {
        _cartProductsLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            _cartProductsLiveData.postValue(
                shopRepository.addProductsToCart(favList, true)
            )
        }
    }
}