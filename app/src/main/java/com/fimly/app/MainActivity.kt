package com.fimly.app

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.fimly.app.presentation.navigation.FimlyNavGraph
import com.fimly.app.presentation.theme.FimlyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FimlyTheme {
                FimlyNavGraph()
            }
        }
    }
}


@Preview(showBackground = true, device = "spec:width=411dp,height=891dp", showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun FimlyPreview() {
    FimlyTheme {
        FimlyNavGraph()
    }
}