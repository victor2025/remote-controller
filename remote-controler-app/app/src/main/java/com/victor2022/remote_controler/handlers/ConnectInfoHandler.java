package com.victor2022.remote_controler.handlers;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.victor2022.remote_controler.utils.PreferenceUtils;

public class ConnectInfoHandler {

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
        return PreferenceUtils.getString(context, INFO_IP, null);
    }

    public static void putName(Context context, String name) {
        PreferenceUtils.putString(context, INFO_NAME, name);
    }

    public static String getName(Context context) {
        return PreferenceUtils.getString(context, INFO_NAME, null);
    }

}
