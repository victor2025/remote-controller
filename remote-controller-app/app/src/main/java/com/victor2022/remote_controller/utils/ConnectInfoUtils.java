package com.victor2022.remote_controller.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import com.victor2022.remote_controller.R;
import com.victor2022.remote_controller.utils.PreferenceUtils;

public class ConnectInfoUtils {

    private static final String INFO_SIGN = "info_sign";
    private static final String INFO_IP = "info_ip";
    private static final String INFO_NAME = "info_name";

    public static void putSign(Context context, boolean sign) {
        PreferenceUtils.putBoolean(context, INFO_SIGN, sign);
    }

    public static boolean getSign(Context context) {
        return PreferenceUtils.getBoolean(context, INFO_SIGN, false);
    }

    public static void putIp(Context context, String ip) {
        PreferenceUtils.putString(context, INFO_IP, ip);
    }

    public static String getIp(Context context) {
        return PreferenceUtils.getString(context, INFO_IP, "none");
    }

    public static void putName(Context context, String name) {
        PreferenceUtils.putString(context, INFO_NAME, name);
    }

    public static String getName(Context context) {
        return PreferenceUtils.getString(context, INFO_NAME, "none");
    }

    // clear connection info
    public static void clearConnectionInfo(Context context){
        putSign(context,false);
        putIp(context, "");
        putName(context, "");
    }

}
