package com.tech.foodzilla.presentation.main.fragment.shop

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tech.foodzilla.R
import com.tech.foodzilla.databinding.FragmentShopBinding
import com.tech.foodzilla.model.*
import com.tech.foodzilla.presentation.MainActivity
import com.tech.foodzilla.presentation.authentication.UserInfoViewModel
import com.tech.foodzilla.presentation.main.adapter.ImageSliderAdapter
import com.tech.foodzilla.presentation.main.adapter.ShopProductAdapter
import com.tech.foodzilla.presentation.main.adapter.ProductItemsAdapter
import com.tech.foodzilla.util.LOADING_ANNOTATION
import com.tech.foodzilla.util.Resource
import com.tech.foodzilla.util.extention.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named


@AndroidEntryPoint
class ShopFragment : Fragment(), ShopProductAdapter.MainProductListener,
    ProductItemsAdapter.ProductListener,
    ImageSliderAdapter.OfferListener {

    private val shopViewModel by activityViewModels<ShopViewModel>()
    private val userInfoViewModel by activityViewModels<UserInfoViewModel>()
    private var searchedText = ""
    private val shopAdapter by lazy { ShopProductAdapter(this, this, this) }

    private val bottomNavigationView by lazy {
        (activity as MainActivity).findViewById<BottomNavigationView>(
            R.id.bottomNavigationView
        )
    }

    @Inject
    @Named(LOADING_ANNOTATION)
    lateinit var loadingDialog: Dialog

    private lateinit var binding: FragmentShopBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_shop, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).showBottomNav()
        observeListener()
    }

    private fun observeListener() {
        // check if user have data into firebase firestore
        userInfoViewModel.userInformationLiveData.observe(viewLifecycleOwner, { userInfo ->
            when (userInfo) {
                is Resource.Success -> {
                    initViews(userInfo.data)
                    shopViewModel.getShopList()
                }
                is Resource.Error -> {
                    loadingDialog.hide()
                    navigateToCreateUserInfoFragment()
                }
                is Resource.Loading -> loadingDialog.show()
            }
        })
        // get products data
        shopViewModel.shopListLiveData.observe(viewLifecycleOwner, { shopList ->
            when (shopList) {
                is Resource.Success -> {
                    shopAdapter.addMainShopListItems(shopList.data)
                    loadingDialog.hide()
                }
                is Resource.Error -> {
                    showToast(shopList.msg!!)
                    loadingDialog.hide()
                }
                is Resource.Loading -> loadingDialog.show()
            }
        })
        // get offer in top of recyclerview as header
        shopViewModel.offersListLiveData.observe(viewLifecycleOwner, { offersList ->
            when (offersList) {
                is Resource.Success ->
                    shopAdapter.addOffersListItems(offersList.data)
                is Resource.Loading -> loadingDialog.show()
            }

        })
        // get products from offer id and navigate to all products fragment.
        shopViewModel.categoryLiveData.observe(viewLifecycleOwner, { category ->
            when (category) {
                is Resource.Success -> {
                    loadingDialog.hide()
                    navigateToAllProductsFragment(category.data!!)
                }
                is Resource.Loading -> loadingDialog.show()
                is Resource.Error -> {
                    loadingDialog.hide()
                }
            }
        })

        // navigate to all products fragment after get products that contain same searched value.
        shopViewModel.searchedProductsLiveData.observe(viewLifecycleOwner, { products ->
            when (products) {
                is Resource.Success -> {
                    val shopItem = MainShopItem(
                        "", searchedText, 0, false,
                        products.data as MutableList<ProductModel>
                    )
                    navigateToAllProductsFragment(shopItem)
                    loadingDialog.hide()
                }
                is Resource.Loading -> loadingDialog.show()
                is Resource.Error -> {
                    showToast(products.msg!!)
                    loadingDialog.hide()
                }
            }
        })
        shopViewModel.cartProductsLiveData.observe(viewLifecycleOwner, {
            when(it){
                is Resource.Success->{
                    showToast(getString(R.string.productAddToCart))
                    shopViewModel.setCartProductValue()
                }
                is Resource.Error-> showToast(it.msg!!)
            }
        })
    }

    private fun initViews(data: UserInfoModel?) {
        if (data == null) return
        binding.apply {
            userNameTextView.text = data.userName
            userImageImageView.loadImage(data.userImage)
            shopRV.adapter = shopAdapter
            shopContainer.show()
            // submit search when click on search button on keyboard.
            shopSearchEditText.searchListener {
                val search = shopSearchEditText.text.toString().trim()
                if (search.isNotEmpty()) {
                    searchedText = search
                    shopViewModel.getProductsHasContainName(searchedText)
                    shopSearchEditText.text.clear()
                }
            }
            // add scale animation to bottom navigation bar when scroll with motion layout.
            shopContainer.transitionBottomNavigationBar({
                bottomNavigationView.animate().scaleY(it)
            }, {
                if (bottomNavigationView.isShown)
                    bottomNavigationView.hide()
                else {
                    showBottomNavigationView()
                }
            })
        }
    }

    private fun showBottomNavigationView() {
        bottomNavigationView.animate().scaleY(1f)
        bottomNavigationView.show()
    }

    private fun navigateToCreateUserInfoFragment() {
        val action = ShopFragmentDirections.actionMainFragmentToCreateUserInfoFragment()
        findNavController().navigate(action)
    }

    private fun navigateToAllProductsFragment(mainShopItem: MainShopItem) {
        val action = ShopFragmentDirections.actionShopFragmentToAllProductsFragment(mainShopItem)
        findNavController().navigate(action)
    }

    override fun onSeeAllClicked(mainShopItem: MainShopItem) {
        navigateToAllProductsFragment(mainShopItem)
    }

    override fun onProductClick(productModel: ProductModel, transitionImageView: ImageView) {
        // add transition to image view when open specific product fragment.
        val extras = FragmentNavigatorExtras(
            transitionImageView to productModel.image
        )
        val action =
            ShopFragmentDirections.actionShopFragmentToSpecificProductFragment(productModel)
        findNavController().navigate(action, extras)

    }

    override fun addProductToCart(productModel: ProductModel) {
        shopViewModel.addProductToCart(productModel.copy())
    }

    // get offer data when click on specific offer from recycler view header .
    override fun onOfferClicked(offersModel: OffersModel) {
        shopViewModel.getSpecificCategoryProducts(offersModel.offerId)
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }

    override fun onStop() {
        super.onStop()
        if (loadingDialog.isShowing)
            loadingDialog.hide()
    }
}