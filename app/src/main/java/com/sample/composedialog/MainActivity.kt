package com.sample.composedialog

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.sample.composedialog.ui.theme.ComposeDialogTheme
import kotlinx.coroutines.delay

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeDialogTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Home(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Home(modifier: Modifier = Modifier) {
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(1000)
        showDialog = true
        delay(5000)
        showDialog = false
    }

    if (showDialog) {
        DialogComposable<HelloDialogFragment>()
    }
}

@Composable
inline fun <reified T : DialogFragment> DialogComposable(crossinline onDismiss: () -> Unit = {}) {
    val view = LocalView.current
    val fragmentManager = remember(view) {
        FragmentManager.findFragmentManager(view)
    }

    DisposableEffect(fragmentManager, T::class.java) {
        val dialogFragment = T::class.java.newInstance()
        dialogFragment.show(fragmentManager, "Dialog ${T::class.java.simpleName}")
        onDispose {
            dialogFragment.dismiss()
            onDismiss()
        }
    }
}
