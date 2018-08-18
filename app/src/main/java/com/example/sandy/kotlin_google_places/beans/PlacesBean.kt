package com.example.sandy.kotlin_google_places.beans

data class PlacesBean(
        val html_attributions: List<Any>,
        val next_page_token: String,
        val results: List<Result>,
        val status: String
)