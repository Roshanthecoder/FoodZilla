package com.tech.foodzilla.presentation.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.tech.foodzilla.BR
import com.tech.foodzilla.R
import com.tech.foodzilla.model.OffersModel
import com.smarteist.autoimageslider.SliderViewAdapter

class ImageSliderAdapter(
    private val list: List<OffersModel>,
    private val offerClickListener: OfferListener
) :
    SliderViewAdapter<ImageSliderAdapter.SliderAdapterViewHolder>() {


    override fun getCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterViewHolder =
        SliderAdapterViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.slider_layout, parent, false
            )
        )


    override fun onBindViewHolder(
        viewHolder: SliderAdapterViewHolder,
        position: Int
    ) {
        viewHolder.bind(list[position])
    }

    inner class SliderAdapterViewHolder(private val binding: ViewDataBinding) :
        SliderViewAdapter.ViewHolder(binding.root) {

        fun bind(offersModel: OffersModel) {
            binding.setVariable(BR.offerModel, offersModel)
            binding.setVariable(BR.offerClickListener, offerClickListener)
        }
    }

    interface OfferListener {
        fun onOfferClicked(offersModel: OffersModel)
    }
}