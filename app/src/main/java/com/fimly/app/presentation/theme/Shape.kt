package com.fimly.app.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    // Tiny: Rating badges, Tab items (8dp)
    extraSmall = RoundedCornerShape(8.dp),
    // Small: Standard Movie Card Containers, Genre Chips (12dp)
    small = RoundedCornerShape(12.dp),
    // Medium: Hero/Carousel Images, Download cards (16dp)
    medium = RoundedCornerShape(16.dp),
    // Large: Search bars / Inputs (24dp)
    large = RoundedCornerShape(24.dp),
    // Extra Large: Buttons (Pill-shaped / 32dp equivalent)
    extraLarge = RoundedCornerShape(percent = 50)
)