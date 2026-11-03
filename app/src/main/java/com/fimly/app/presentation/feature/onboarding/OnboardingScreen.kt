package com.fimly.app.presentation.feature.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fimly.app.R
import com.fimly.app.presentation.theme.FimlyTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is OnboardingEffect.NavigateToMainApp -> onFinishOnboarding()
            }
        }
    }

    OnboardingContent(
        onGetStartedClick = {
            viewModel.onIntent(OnboardingIntent.CompleteOnboardingClick)
        }
    )
}

@Composable
fun OnboardingContent(
    onGetStartedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // گرفتن رنگ پس‌زمینه از تم جاری برای پشتیبانی ۱۰۰٪ از لایت و دارک مود
    val backgroundColor = MaterialTheme.colorScheme.background

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // 1. Full-Screen Background Image
        Image(
            painter = painterResource(id = R.drawable.bg_onboarding),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Gradient Overlay (Adapts perfectly to Light/Dark Mode)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            backgroundColor.copy(alpha = 0.1f),
                            backgroundColor.copy(alpha = 0.5f),
                            backgroundColor
                        )
                    )
                )
        )

        // 3. Bottom Card Container
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Headline for Fimly
                Text(
                    text = "Welcome to Fimly",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Fimly Subtitle Copy
                Text(
                    text = "Discover top-rated movies, explore new releases, and build your ultimate offline watchlist.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Full-width Get Started Button (Replaces dots and side-button for a single-page flow)
                Button(
                    onClick = onGetStartedClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Get Started",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back), // Keep your arrow icon if you like
                        contentDescription = null,
                        modifier = Modifier.size(20.dp).rotate(180f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Onboarding - Dark Mode")
@Composable
private fun OnboardingDarkPreview() {
    FimlyTheme(darkTheme = true) {
        OnboardingContent(onGetStartedClick = {})
    }
}

@Preview(showBackground = true, name = "Onboarding - Light Mode")
@Composable
private fun OnboardingLightPreview() {
    FimlyTheme(darkTheme = false) {
        OnboardingContent(onGetStartedClick = {})
    }
}