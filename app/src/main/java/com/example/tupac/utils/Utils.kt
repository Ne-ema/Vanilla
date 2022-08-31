package com.example.tupac.utils

import android.app.Dialog
import com.example.tupac.R
import com.google.firebase.database.core.Context

private lateinit var dialog: Dialog
fun getDialogBox(context: android.content.Context) {
    dialog = Dialog(context)
    dialog.setContentView(R.layout.loading_dialog)
    dialog.setCancelable(false)
    dialog.show()
}

fun dismissDialogBox() {
    dialog.dismiss()
}