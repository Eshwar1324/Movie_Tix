package com.kyc.project1.Models

data class BookedTickets(
    val posterUrl: String,
    val title: String,
    val date: String,
    val time: String,
    val seats: String,
    val numberOfTickets: Int,
    val totalPrice: Double
)
