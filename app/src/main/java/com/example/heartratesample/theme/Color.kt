package com.example.heartratesample.theme

import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)
val Red400 = Color(0xFFCF6679)
val Heart = Color(0xFFFF0055)

internal val wearColorPalette: Colors = Colors(
    primary = Red400,
    primaryVariant = Heart,
    secondary = Heart,
    secondaryVariant = Heart,
    error = Red400,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onError = Color.Black
)
