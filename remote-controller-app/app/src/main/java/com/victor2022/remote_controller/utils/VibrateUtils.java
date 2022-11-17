package com.victor2022.remote_controller.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;

public class VibrateUtils {

    public static void shortVibration(Context context){
        vibrate(context, 50);
    }

    public static void longVibration(Context context){
        vibrate(context, 200);
    }

    public static void vibrate(Context context, long dur){
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(dur);
    }
}
