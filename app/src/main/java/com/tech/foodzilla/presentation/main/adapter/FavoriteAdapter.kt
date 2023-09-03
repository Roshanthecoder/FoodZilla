package com.tech.foodzilla.presentation.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.tech.foodzilla.BR
import com.tech.foodzilla.R
import com.tech.foodzilla.model.ProductModel
import javax.inject.Inject

class FavoriteAdapter
@Inject
constructor() : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    private val productList = mutableListOf<ProductModel>()
    private lateinit var favListener: FavoriteProductListener
    fun addProducts(list: List<ProductModel>, listener: FavoriteProductListener) {
        favListener = listener
        productList.apply {
            clear()
            addAll(list)
            if (list.isNotEmpty())
                add(list[0])
        }
        notifyDataSetChanged()
    }

    fun getAllFavoriteProducts() = productList

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteAdapter.FavoriteViewHolder =
        FavoriteViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.favorite_item_rv_layout,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: FavoriteAdapter.FavoriteViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size

    inner class FavoriteViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(productModel: ProductModel) = with(binding) {
            setVariable(BR.productItem, productModel)
            setVariable(BR.favListener, favListener)
            val isLastItem = adapterPosition == productList.size - 1
            setVariable(BR.isLastItem, isLastItem)
        }
    }

    interface FavoriteProductListener {
        fun onFavProductClick(productModel: ProductModel, favProductImage: ImageView)
    }

}