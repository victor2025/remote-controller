package com.victor2022.remote_controller.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    public static void showToast(Activity activity, String msg){
        activity.runOnUiThread(()->{
            Toast toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
            toast.show();
        });
    }
}
