package com.tech.foodzilla.presentation.main.fragment.account.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.tech.foodzilla.R
import com.tech.foodzilla.databinding.FragmentTrackOrdersBinding
import com.tech.foodzilla.util.extention.closeFragment


class TrackOrdersFragment : Fragment() {

    private lateinit var binding: FragmentTrackOrdersBinding
    private val args by navArgs<TrackOrdersFragmentArgs>()
    private val orderModel by lazy { args.orderModel }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_track_orders, container, false)
        return binding.run {
            fragment = this@TrackOrdersFragment
            order = orderModel
            root
        }
    }

    fun backPressFragment() = closeFragment()

}