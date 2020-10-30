package com.gov.mpt.laokyclib.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.kinda.alert.KAlertDialog

import android.graphics.Color
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.gov.mpt.laokyclib.R

/**
 * Created by SBlab on 2020-10-29.
 */

class ProgressDialogUtil (val context: Context) {

    private var dialog: AlertDialog? = null
    private var pDialog: KAlertDialog? = null
    var dialogProgress : Dialog? = null

    fun showDialogProgress(TitleProgress: String) {
        dialogProgress = Dialog(context)
        dialogProgress!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogProgress!!.setCancelable(false)
        dialogProgress!!.setContentView(R.layout.dialog_progress)
        val tvLabelDialogProgress = dialogProgress!!.findViewById(R.id.tvLabelDialogProgress) as TextView
        tvLabelDialogProgress!!.text = TitleProgress

        dialogProgress!!.show()

    }

    fun dismissDialogProgress() {
        dialogProgress!!.dismiss()
    }


    fun showKAlertDialog(message : String , flg : String ){
        if (flg == "success") {
            pDialog = KAlertDialog(context, KAlertDialog.SUCCESS_TYPE)
            pDialog!!.changeAlertType(KAlertDialog.SUCCESS_TYPE)
        } else if ( flg == "error") {
            pDialog = KAlertDialog(context, KAlertDialog.ERROR_TYPE)
            pDialog!!.changeAlertType(KAlertDialog.ERROR_TYPE)
        } else if ( flg == "warning") {
            pDialog = KAlertDialog(context, KAlertDialog.WARNING_TYPE)
            pDialog!!.changeAlertType(KAlertDialog.WARNING_TYPE)
        }

        pDialog!!.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog!!.titleText = "ແຈ້ງເຕືອນ"
        pDialog!!.contentText = message
        pDialog!!.confirmText = "Dismiss"


        pDialog!!.show()
    }

}