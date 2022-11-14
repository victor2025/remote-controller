package com.victor2022.remote_controler.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {

    /**
     * 读取布尔值方法
     * @param activity
     * @param key
     * @param defValue
     * @return
     */
    public static Boolean getBoolean(Context activity, String key, Boolean defValue){
        SharedPreferences preferences = activity.getSharedPreferences("data", MODE_PRIVATE);
        return preferences.getBoolean(key,defValue);
    }

    /**
     * 存放布尔值方法
     * @param activity
     * @param key
     * @param value
     */
    public static void putBoolean(Context activity,String key,Boolean value){
        SharedPreferences.Editor editor = activity.getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putBoolean(key,value);
        editor.commit();
    }
    /**
     * 读取字符串方法
     * @param activity
     * @param key
     * @param defValue
     * @return
     */
    public static String getString(Context activity,String key,String defValue){
        SharedPreferences preferences = activity.getSharedPreferences("data", MODE_PRIVATE);
        return preferences.getString(key,defValue);
    }

    /**
     * 存放字符串方法
     * @param activity
     * @param key
     * @param value
     */
    public static void putString(Context activity,String key,String value){
        SharedPreferences.Editor editor = activity.getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putString(key,value);
        editor.commit();
    }
}
