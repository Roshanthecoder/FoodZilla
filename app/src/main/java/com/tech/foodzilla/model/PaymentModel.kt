package com.tech.foodzilla.model

data class PaymentModel(
    val amount: String,
    val currency: String = "usd",
    val paymentMethodType: String = "card"
)