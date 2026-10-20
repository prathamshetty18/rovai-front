package com.roavai.parent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.roavai.parent.data.fake.DemoSettings
import com.roavai.parent.ui.navigation.RoavaiNavHost
import com.roavai.parent.ui.theme.RoavaiTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var demoSettings: DemoSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoavaiTheme {
                RoavaiNavHost(demoSettings = demoSettings)
            }
        }
    }
}
