package com.victor2022.remote_controller.handlers;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.victor2022.remote_controller.R;
import com.victor2022.remote_controller.utils.DeviceScanner;
import com.victor2022.remote_controller.utils.NetworkUtils;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectBarHandler {

    private Activity activity;
    private TextView view;
    private static DeviceScanner scanner;
    private static AtomicBoolean isRunning = new AtomicBoolean(false);

    public ConnectBarHandler(Activity activity, View view) {
        this.activity = activity;
        this.view = (TextView) view;
        if (scanner == null) {
            scanner = new DeviceScanner();
        }
    }

    // process connect bar
    public void handle() {
        if (!isRunning.get()) {
            synchronized (activity) {
                if (!isRunning.get()) {
                    handle0();
                }
            }
        }
    }

    // handle with another thread
    private void handle0() {
        new Thread() {
            @Override
            public void run() {
                isRunning.set(true);
                // set background color
                activity.runOnUiThread(() -> {
                    view.setBackgroundColor(activity.getResources().getColor(R.color.status_connecting));
                });
                // start scan
                String[] res = scan();
                // scan and connect
                if (res == null) {
                    disconnected();
                }
                connected(res);
                isRunning.set(false);
            }
        }.start();
    }

    // scan and connect
    private String[] scan() {

        String localIp = NetworkUtils.getLocalIPAddress(activity);
        // start check
        int headerIdx = localIp.lastIndexOf('.');
        String header = localIp.substring(0, headerIdx + 1);
        String self = localIp.substring(headerIdx + 1);
        SynchronousQueue<String[]> queue = new SynchronousQueue<>();
        // use another thread to submit task
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i <= 0; i++) {
                    if (!self.equals(String.valueOf(i))) {
                        String ip = header + String.valueOf(i);
                        scanner.check(ip, queue);
                    }
                }
            }
        }.start();
        // check result
        int dur = 30;
        String connStr = activity.getResources().getString(R.string.connecting_status);
        while (dur >= 0 && queue.isEmpty()) {
            String text = connStr + dur;
            activity.runOnUiThread(() -> {
                view.setText(text);
            });
            dur--;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // process results
        scanner.terminate();
        if (queue.size() == 0) {
            return null;
        }
        return queue.poll();
    }

    // save info and handle bar
    private void connected(String[] ip) {
        if (ip != null) {
            saveInfo(true, ip);
            activity.runOnUiThread(() -> {
                // color
                view.setBackgroundColor(activity.getColor(R.color.status_connected));
                // text
                String devName = ip[1];
                String text = activity.getString(R.string.connected_status) + devName;
                view.setText(text);
            });
        }
    }

    // save info and handle bar
    private void disconnected() {
        saveInfo(false, null);
        activity.runOnUiThread(()-> {
            // color
            view.setBackgroundColor(activity.getColor(R.color.status_disconnected));
            // text
            view.setText(activity.getString(R.string.default_status));
        });
    }

    private void saveInfo(boolean success, String[] ip) {
        if (success && ip != null) {
            ConnectInfoHandler.putSign(activity, true);
            ConnectInfoHandler.putIp(activity, ip[0]);
            ConnectInfoHandler.putName(activity, ip[1]);
        } else {
            ConnectInfoHandler.putSign(activity, false);
        }
    }
}
