package com.victor2022.remote_controller.handlers;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.victor2022.remote_controller.utils.HttpUtils;
import com.victor2022.remote_controller.utils.StringUtils;
import com.victor2022.remote_controller.utils.ToastUtils;
import com.victor2022.remote_controller.utils.VibrateUtils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ControlPanelHandler {

    private static final String SWITCH_DOMAIN = "/switch/";
    private static final String POWER_DOMAIN = "/power/";
    private static final String MODE_DOMAIN = "/mode/";
    private static final String UP_DOMAIN = "/up/";
    private static final String DOWN_DOMAIN = "/down/";
    private Activity activity;
    private ThreadPoolExecutor pool;

    public ControlPanelHandler(Activity activity) {
        this.activity = activity;
    }

    // execute when title is clicked
    public void handleSwitch(){
        sendAndProcess(SWITCH_DOMAIN);
    }
    // execute when power button is clicked
    public void handlePower(){
        sendAndProcess(POWER_DOMAIN);
    }
    // execute when mode button is clicked
    public void handleMode(){
        sendAndProcess(MODE_DOMAIN);
    }
    // execute when up button is clicked
    public void handleUp(){
        sendAndProcess(UP_DOMAIN);
    }
    // execute when down button is clicked
    public void handleDown(){
        sendAndProcess(DOWN_DOMAIN);
    }

    // send request and process response
    private void sendAndProcess(String domain){
        if (pool==null){
            // create pool
            this.pool = new ThreadPoolExecutor(2,4,5, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(20),
                    new ThreadPoolExecutor.DiscardPolicy());
        }
        this.pool.submit(()->{
            if(!ConnectStatusHandler.isConnected())return;
            // get ip and make address
            String ip = ConnectInfoHandler.getDeviceIp();
            String address = HttpUtils.HTTP_PREFIX+ip+domain;
            // send request and get response
            String resp = HttpUtils.httpGet(address, 500, 800);
            // vibrate
            if(!StringUtils.isEmpty(resp)){
                Log.i(this.getClass().getName(), "request to: "+address);
                VibrateUtils.shortVibration(activity);
            }
        });
    }

}
