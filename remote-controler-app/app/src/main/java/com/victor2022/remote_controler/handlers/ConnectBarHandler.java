package com.victor2022.remote_controler.handlers;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.victor2022.remote_controler.R;
import com.victor2022.remote_controler.utils.DeviceScanner;
import com.victor2022.remote_controler.utils.NetworkUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectBarHandler {

    private Context context;
    private TextView view;
    private static DeviceScanner scanner;
    private static AtomicBoolean isRunning = new AtomicBoolean(false);

    public ConnectBarHandler(Context context, View view) {
        this.context = context;
        this.view = (TextView) view;
        if (scanner == null) {
            scanner = new DeviceScanner();
        }
    }

    // process connect bar
    public void handle() {
        if(!isRunning.get()){
            handle0();
        }
    }

    // handle with another thread
    private void handle0() {
        isRunning.set(true);
        // set background color
        view.setBackgroundColor(context.getResources().getColor(R.color.status_connecting));
        // start scan
        String[] res = scan();
        // scan and connect
        if (res == null) {
            disconnected();
        }
        connected(res);
        isRunning.set(false);
    }

    // scan and connect
    private String[] scan() {
        String connStr = context.getResources().getString(R.string.connecting_status);
        String localIp = NetworkUtils.getLocalIPAddress(context);
        // start check
        int headerIdx = localIp.lastIndexOf('.');
        String header = localIp.substring(0, headerIdx + 1);
        String self = localIp.substring(headerIdx + 1);
        List<String[]> list = new CopyOnWriteArrayList<>();
        // use another thread to submit task
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i <= 255; i++) {
                    if (!self.equals(String.valueOf(i))) {
                        String ip = header + String.valueOf(i);
                        scanner.check(ip, list);
                    }
                }
            }
        }.start();
        // check result
        int dur = 30;
        while (dur >= 0 && list.size() == 0) {
            String text = connStr + dur;
            view.setText(text);
            dur--;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // process results
        scanner.terminate();
        if (list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    // save info and handle bar
    private void connected(String[] ip) {
        saveInfo(true, ip);
        // color
        view.setBackgroundColor(context.getColor(R.color.status_connected));
        // text
        String devName = ip[1];
        String text = context.getString(R.string.connected_status)+devName;
        view.setText(text);
    }

    // save info and handle bar
    private void disconnected() {
        saveInfo(false, null);
        // color
        view.setBackgroundColor(context.getColor(R.color.status_disconnected));
        // text
         view.setText(context.getString(R.string.default_status));
    }

    private void saveInfo(boolean success, String[] ip) {
        if (success) {
            ConnectInfoHandler.putSign(context, true);
            ConnectInfoHandler.putIp(context, ip[0]);
            ConnectInfoHandler.putName(context, ip[1]);
        }else{
            ConnectInfoHandler.putSign(context,false);
        }
    }
}
