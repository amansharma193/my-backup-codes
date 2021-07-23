package com.transportervendor;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class CustomProgressDialog extends Dialog {
     TextView tvtxt;
    public CustomProgressDialog(Context context, String text) {
        super(context);
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        View view = LayoutInflater.from(context).inflate(
                R.layout.pgdialog, null);
        setContentView(view);
        tvtxt=view.findViewById(R.id.tvtxt);
        tvtxt.setText(text);
    }
}