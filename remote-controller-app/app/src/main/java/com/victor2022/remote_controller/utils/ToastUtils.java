package com.victor2022.remote_controller.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    public static void showToast(Context context, String msg){
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}
