package com.victor2022.remote_controller.handlers;

import android.app.Activity;
import android.widget.TextView;

import com.victor2022.remote_controller.R;
import com.victor2022.remote_controller.utils.VibrateUtils;

import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectStatusHandler {

    private static AtomicBoolean isConnected = new AtomicBoolean(false);

    // save or update connect info, show connect info on bar and info zone
    public static void connected(Activity activity, String deviceName, String deviceIp){
        // main connect flag
        setConnectStatus(true);
        // show
        ConnectInfoHandler.saveAndFlushInfo(activity, true, deviceName, deviceIp);
        // handle bar
        TextView bar = activity.findViewById(R.id.connect_bar);
        activity.runOnUiThread(() -> {
            // color
            bar.setBackgroundColor(activity.getColor(R.color.status_connected));
            // text
            String text = activity.getString(R.string.connected_status);
            text = String.format(text, deviceName);
            bar.setText(text);
        });
        // vibrate
        VibrateUtils.longVibration(activity);
    }

    // clear connect info, show disconnect info on bar and info zone
    public static void disconnected(Activity activity){
        // handle main flag
        setConnectStatus(false);
        // save to preferences and flush info window
        ConnectInfoHandler.saveAndFlushInfo(activity, false, null, null);
        // handle bar
        TextView bar = activity.findViewById(R.id.connect_bar);
        activity.runOnUiThread(() -> {
            // color
            bar.setBackgroundColor(activity.getColor(R.color.status_disconnected));
            // text
            bar.setText(activity.getString(R.string.default_status));
        });
    }

    // return connect status
    public static boolean isConnected(){
        return isConnected.get();
    }

    // set connect status
    public static void setConnectStatus(boolean status){
        isConnected.set(status);
    }
}
