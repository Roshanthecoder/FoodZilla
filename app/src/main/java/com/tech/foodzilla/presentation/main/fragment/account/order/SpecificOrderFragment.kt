package com.tech.foodzilla.presentation.main.fragment.account.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tech.foodzilla.R
import com.tech.foodzilla.databinding.FragmentSpecificOrderBinding
import com.tech.foodzilla.presentation.main.adapter.OrderProductsAdapter
import com.tech.foodzilla.util.extention.closeFragment


class SpecificOrderFragment : Fragment() {

    private lateinit var binding: FragmentSpecificOrderBinding
    private val args by navArgs<SpecificOrderFragmentArgs>()
    private val orderModel by lazy { args.order }
    private val specificOrdersAdapter by lazy { OrderProductsAdapter(orderModel.productsList) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_specific_order, container, false)
        return binding.run {
            fragment = this@SpecificOrderFragment
            order = orderModel
            adapter = specificOrdersAdapter
            root
        }
    }

    fun trackOrder(){
        val action = SpecificOrderFragmentDirections.actionSpecificOrderFragmentToTrackOrdersFragment(orderModel)
        findNavController().navigate(action)
    }

    fun backPressFragment() = closeFragment()
}