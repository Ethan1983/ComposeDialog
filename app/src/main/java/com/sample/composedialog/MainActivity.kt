package com.sample.composedialog

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.sample.composedialog.ui.theme.ComposeDialogTheme

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
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Button(onClick = { showDialog = true }) {
            Text(text = "Show Dialog")
        }
    }

    if (showDialog) {
        DialogComposable<HelloDialogFragment> {
            showDialog = false
        }
    }
}

@Composable
inline fun <reified T : DialogFragment> DialogComposable(crossinline onDismiss: () -> Unit = {}) {
    val view = LocalView.current
    var dismissNotified by remember { mutableStateOf(false) }
    val fragmentManager = remember(view) {
        FragmentManager.findFragmentManager(view)
    }

    val fragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            if (f is T) {
                onDismiss()
                dismissNotified = true
            }
        }
    }

    DisposableEffect(fragmentManager, T::class.java) {
        val dialogFragment = T::class.java.newInstance()
        fragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)
        dialogFragment.show(fragmentManager, "Dialog ${T::class.java.simpleName}")

        onDispose {
            fragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
            dialogFragment.dismiss()
            if (!dismissNotified) {
                onDismiss()
            }
        }
    }
}
