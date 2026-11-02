package com.fimly.app.presentation.feature.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ProfileScreen() {

    Box(modifier = Modifier.fillMaxSize()) {
        Text("Coming soon", modifier = Modifier.align(Alignment.Center), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
    }
}