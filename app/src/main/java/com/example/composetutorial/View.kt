package com.example.composetutorial

sealed class View(val route: String) {
    data object MainView : View("main_view")
    data object SecondaryView : View("secondary_view")
}