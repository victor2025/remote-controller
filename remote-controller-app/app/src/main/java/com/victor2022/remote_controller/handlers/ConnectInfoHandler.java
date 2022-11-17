package com.victor2022.remote_controller.handlers;

import android.app.Activity;
import android.widget.TextView;

import com.victor2022.remote_controller.R;
import com.victor2022.remote_controller.utils.ConnectInfoUtils;
import com.victor2022.remote_controller.utils.HttpUtils;

/**
 * handle connect info
 */
public class ConnectInfoHandler {

    private static String deviceIp = null;
    private static String deviceName = null;

    // save and flush info
    public static void saveAndFlushInfo(Activity activity, boolean success, String deviceName, String deviceIp) {
        synchronized (activity){
            // save info to ram and disk
            saveInfo(activity, success, deviceName, deviceIp);
            // flush info on the view
            flushInfo(activity);
        }
    }

    // save info to preferences
    public static void saveInfo(Activity activity, boolean success, String deviceName, String deviceIp) {
        if (success) {
            // save to rom
            ConnectInfoUtils.putSign(activity, true);
            ConnectInfoUtils.putName(activity, deviceName);
            ConnectInfoUtils.putIp(activity, deviceIp);
            // save to ram
            setDeviceIp(deviceIp);
            setDeviceName(deviceName);
        } else {
            ConnectInfoUtils.putSign(activity, false);
        }
    }

    // flush info window
    public static void flushInfo(Activity activity) {
        activity.runOnUiThread(() -> {
            boolean success = ConnectStatusHandler.isConnected();
            // get view
            TextView nameView = activity.findViewById(R.id.info_name);
            TextView addressView = activity.findViewById(R.id.info_address);
            TextView confPageView = activity.findViewById(R.id.info_config);
            TextView statusView = activity.findViewById(R.id.info_status);
            // info
            String name = "none";
            String address = "none";
            String status = "disconnected";
            int statusColor = activity.getColor(R.color.status_disconnected);
            String confAddr = "none";
            confPageView.setLinksClickable(false);
            // check status
            if (success) {
                name = getDeviceName();
                address = getDeviceIp();
                confAddr = HttpUtils.HTTP_PREFIX+address;
                confPageView.setLinksClickable(true);
                status = "connected";
                statusColor = activity.getColor(R.color.status_connected);
            }

            // update view
            nameView.setText(name);
            addressView.setText(address);
            confPageView.setText(confAddr);
            statusView.setText(status);
            statusView.setTextColor(statusColor);
        });
    }

    // load connection status from disk
    public static boolean getPreviousConnectStatus(Activity activity){
        boolean res = false;
        synchronized (activity){
            res = ConnectInfoUtils.getSign(activity);
        }
        return res;
    }

    public static String getDeviceIp() {
        return deviceIp;
    }

    public static void setDeviceIp(String deviceIp) {
        ConnectInfoHandler.deviceIp = deviceIp;
    }

    public static String getDeviceName() {
        return deviceName;
    }

    public static void setDeviceName(String deviceName) {
        ConnectInfoHandler.deviceName = deviceName;
    }
}
