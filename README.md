# ComposeDialog

To launch a DialogFragment from Composable world. Recently added AndroidFragment composable from
androidx.fragment:fragment-compose:1.8.0 doesn't support DialogFragments.

```
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
```
