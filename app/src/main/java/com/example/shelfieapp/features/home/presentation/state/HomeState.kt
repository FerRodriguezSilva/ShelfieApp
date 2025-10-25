package com.example.shelfieapp.features.home.presentation.state

import com.example.shelfieapp.features.home.domain.model.HomeData

data class HomeState(
    val homeData: HomeData = HomeData(),
    val isLoading: Boolean = false,
    val isDrawerOpen: Boolean = false
)