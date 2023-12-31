package com.tech.foodzilla.presentation.main.fragment.account.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tech.foodzilla.R
import com.tech.foodzilla.databinding.FragmentOrderStatusBinding
import com.tech.foodzilla.util.extention.closeFragment

class OrderStatusFragment : Fragment() {

    private lateinit var binding: FragmentOrderStatusBinding
    private val args by navArgs<OrderStatusFragmentArgs>()
    private val mIsOrderSubmitted by lazy { args.isOrderSubmitted }
    private val orderModel by lazy { args.orderModel }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_status, container, false)
        return binding.run {
            fragment = this@OrderStatusFragment
            isOrderSubmitted = mIsOrderSubmitted
            root
        }
    }

    fun trackOrTryAgain(){
        if(mIsOrderSubmitted){
            navigateToTrackOrderFragment()
        }else{
            closeFragment()
        }
    }

    private fun navigateToTrackOrderFragment() {
        val action = OrderStatusFragmentDirections.actionOrderStatusFragmentToTrackOrdersFragment(orderModel)
        findNavController().navigate(action)
    }

    fun backToHome(){
       val action = OrderStatusFragmentDirections.actionOrderStatusFragmentToShopFragment()
        findNavController().navigate(action)
    }
}