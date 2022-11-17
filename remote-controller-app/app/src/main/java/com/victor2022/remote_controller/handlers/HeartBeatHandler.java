package com.victor2022.remote_controller.handlers;

import android.app.Activity;
import android.util.Log;

import com.victor2022.remote_controller.utils.ConnectInfoUtils;
import com.victor2022.remote_controller.utils.HttpUtils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class HeartBeatHandler {

    private static final String HEART_BEAT_DOMAIN = "/identity/";

    // check previous connection
    public void checkPreviousConnection(Activity activity) {
        new Thread() {
            @Override
            public void run() {
                if (ConnectInfoHandler.getPreviousConnectStatus(activity)) {

                    String ip = ConnectInfoUtils.getIp(activity);
                    boolean isConnected = beat(ip);
                    if (isConnected) {
                        String name = ConnectInfoUtils.getName(activity);
                        ConnectStatusHandler.connected(activity, name, ip);
                    }
                }
            }
        }.start();
    }

    // start heart beat
    public void startHeartBeat(Activity activity) {
        startHeartBeatTask(activity);
    }

    // start heart beat with timer task
    private void startHeartBeatTask(Activity activity) {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                checkAndSetInfo(activity);
            }
        };
        timer.schedule(timerTask, 1000, 3000);
    }

    // check connection status and set connect info if the connection is invalid
    private void checkAndSetInfo(Activity activity) {
        boolean isConnected = ConnectStatusHandler.isConnected();
        if (!isConnected) return;
        // if isConnected check connection
        String ip = ConnectInfoHandler.getDeviceIp();
        isConnected = beat(ip);
        // if the connection is invalid
        if (!isConnected) {
            ConnectStatusHandler.disconnected(activity);
        }
    }

    // heart beat by trying communicating with device
    private boolean beat(String ip) {
        Log.i(this.getClass().getName(), "ping: sending heart beat request");
        String url = HttpUtils.HTTP_PREFIX + ip + HEART_BEAT_DOMAIN;
        String resp = HttpUtils.httpGet(url, 2000, 2000);
        Log.i(this.getClass().getName(), "pong: " + resp);
        return resp != null && !"".equals(resp);
    }

}
