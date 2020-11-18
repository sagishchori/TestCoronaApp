package com.sagish.testcoronaapp.ui.helpers

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

class AlertDialogHelper {

    companion object {

        /**
         * A method to set an AlertDialog with control over the clicked positive button
         */
        fun show(context : Context,
                 message : Int,
                 positiveText : Int,
                 positiveButtonClickListener : DialogInterface.OnClickListener) {
            getDialog(context)
                .setMessage(message)
                .setPositiveButton(positiveText, positiveButtonClickListener)
                .show()
        }

        fun show(context : Context,
                 message : String,
                 positiveText : Int,
                 positiveButtonClickListener : DialogInterface.OnClickListener) {
            getDialog(context)
                .setMessage(message)
                .setPositiveButton(positiveText, positiveButtonClickListener)
                .show()
        }

        /**
         * A method to set an AlertDialog without control over the clicked positive button
         */
        fun show(context : Context,
                 message : Int,
                 positiveText : Int) {
            show(context, message, positiveText, object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    // Do nothing
                }
            })
        }

        fun show(context : Context,
                 message : String,
                 positiveText : Int) {
            show(context, message, positiveText, object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    // Do nothing
                }
            })
        }

        private fun getDialog(context: Context): AlertDialog.Builder {
            return AlertDialog.Builder(context)
        }
    }
}