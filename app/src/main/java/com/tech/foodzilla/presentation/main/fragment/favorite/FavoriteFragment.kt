package com.tech.foodzilla.presentation.main.fragment.favorite

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.tech.foodzilla.R
import com.tech.foodzilla.databinding.FragmentFavoriteBinding
import com.tech.foodzilla.model.ProductModel
import com.tech.foodzilla.presentation.main.adapter.FavoriteAdapter
import com.tech.foodzilla.util.LOADING_ANNOTATION
import com.tech.foodzilla.util.Resource
import com.tech.foodzilla.util.extention.hide
import com.tech.foodzilla.util.extention.show
import com.tech.foodzilla.util.extention.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class FavoriteFragment : Fragment(R.layout.fragment_favorite),
    FavoriteAdapter.FavoriteProductListener {

    @Inject
    lateinit var favoriteAdapter: FavoriteAdapter

    @Inject
    @Named(LOADING_ANNOTATION)
    lateinit var loadingDialog: Dialog

    private lateinit var binding: FragmentFavoriteBinding
    private val favoriteViewModel by viewModels<FavoriteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favoriteViewModel.getFavoriteProducts()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite, container, false)
        binding.adapter = favoriteAdapter
        binding.fragment = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeListener()
    }

    private fun observeListener() {
        favoriteViewModel.favoriteProductsLiveData.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                binding.emptyProducts.show()
                binding.favoriteContainer.hide()
            } else {
                favoriteAdapter.addProducts(it, this)
                binding.emptyProducts.hide()
                binding.favoriteContainer.show()
            }
        })

        favoriteViewModel.cartProductsLiveData.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    showToast(getString(R.string.savedToCart))
                    loadingDialog.hide()
                }
                is Resource.Error -> {
                    showToast(it.msg!!)
                    loadingDialog.hide()
                }
                is Resource.Loading -> loadingDialog.show()
            }
        })
    }

    fun addAllToCart() {
        val favList = favoriteAdapter.getAllFavoriteProducts()
        favoriteViewModel.addProductsToCart(favList)
    }

    override fun onFavProductClick(productModel: ProductModel, favProductImage: ImageView) {
        navigateToSpecificProductFragment(productModel, favProductImage)
    }

    private fun navigateToSpecificProductFragment(
        productModel: ProductModel,
        transitionImageView: ImageView
    ) {
        // add transition to image view when open specific product fragment.
        val extras = FragmentNavigatorExtras(
            transitionImageView to productModel.image
        )
        val action =
            FavoriteFragmentDirections.actionFavoriteFragmentToSpecificProductFragment(productModel)
        findNavController().navigate(action, extras)
    }

}