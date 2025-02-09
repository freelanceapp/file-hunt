package com.mojodigi.filehunt.Utils;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

public class CustomProgressDialog {

    private static ProgressDialog progressDialog;

    public static void show(final Context context, String messageResourceString) {


        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        int style;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            style = android.R.style.Theme_Material_Light_Dialog;
        } else {
            //noinspection deprecation
            style = ProgressDialog.THEME_HOLO_LIGHT;

        }

        progressDialog = new ProgressDialog(context, style);
        progressDialog.setMessage(messageResourceString);
        progressDialog.setCancelable(true);

        boolean isActivityFinishing=((Activity)context).isFinishing();
        if(!isActivityFinishing)
        progressDialog.show();


    }

    public static void dismiss() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }

    }



}