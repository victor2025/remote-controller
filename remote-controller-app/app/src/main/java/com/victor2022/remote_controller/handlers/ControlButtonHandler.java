package com.victor2022.remote_controller.handlers;

import android.app.Activity;

import com.victor2022.remote_controller.utils.ConnectInfoUtils;
import com.victor2022.remote_controller.utils.HttpUtils;

public class ControlButtonHandler {

    private static final String POWER_DOMAIN = "/switch/";

    // execute when power button is clicked
    public static void handlePower(Activity activity){
        if(!isConnected(activity))return;
        // get ip
        String ip = ConnectInfoUtils.getIp(activity);
        String address = HttpUtils.HTTP_PREFIX+ip+POWER_DOMAIN;
        // request
        new Thread(){
            @Override
            public void run() {
                HttpUtils.httpPost(address,null,500,1000);
            }
        }.start();
    }

    private static boolean isConnected(Activity activity){
        return ConnectInfoUtils.getSign(activity);
    }
}
