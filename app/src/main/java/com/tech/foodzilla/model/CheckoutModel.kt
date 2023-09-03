package com.tech.foodzilla.model

data class CheckoutModel (
    val userAddress: String,
    val totalCost: Float,
    val paymentMethod: String
        )