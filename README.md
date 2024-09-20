# ComposeDialog

To launch a DialogFragment from Composable world. Recently added AndroidFragment composable from
androidx.fragment:fragment-compose:1.8.0 doesn't support DialogFragments.

```
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
```
