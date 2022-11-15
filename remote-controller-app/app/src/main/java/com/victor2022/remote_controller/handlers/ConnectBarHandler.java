package com.victor2022.remote_controller.handlers;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.victor2022.remote_controller.R;
import com.victor2022.remote_controller.utils.ConnectInfoUtils;
import com.victor2022.remote_controller.utils.NetworkUtils;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectBarHandler {

    private Activity activity;
    private TextView view;
    private static DeviceScanner scanner;
    private static AtomicBoolean isRunning = new AtomicBoolean(false);
    private static AtomicBoolean isConnected = new AtomicBoolean(false);

    public ConnectBarHandler(Activity activity, View view) {
        this.activity = activity;
        this.view = (TextView) view;
        if (scanner == null) {
            scanner = new DeviceScanner();
        }
    }

    // process connect bar
    public void handle() {
        if (!isRunning.get() && !isConnected.get()) {
            // try to connect
            synchronized (activity) {
                if (!isRunning.get() && !isConnected.get()) {
                    handle0();
                }
            }
        } else {
            handle1();
        }

    }


    // handle with another thread
    private void handle0() {
        new Thread() {
            @Override
            public void run() {
                isRunning.compareAndSet(false, true);
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
                isRunning.compareAndSet(true, false);
            }
        }.start();
    }

    // stop all the actions
    private void handle1() {
        new Thread() {
            @Override
            public void run() {
                disconnected();
                isRunning.compareAndSet(true, false);
            }
        }.start();
    }

    // scan and connect
    private String[] scan() {
        CountDownLatch scanWait = new CountDownLatch(1);
        Queue<String[]> queue = new ConcurrentLinkedQueue<>();
        // submit scan tasks
        submitScanTasks(queue, scanWait);
        // wait for scanning
        new Thread() {
            @Override
            public void run() {
                int dur = 30;
                String connStr = activity.getResources().getString(R.string.connecting_status);
                // show status
                while (dur >= 0 && isRunning.get()) {
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
                // count down
                scanWait.countDown();
            }
        }.start();
        // wait for result
        try {
            scanWait.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // terminate
        scanner.terminate();
        // process results
        if (queue.isEmpty()) {
            return null;
        }
        return queue.poll();
    }

    // submit scan task to check
    private void submitScanTasks(Queue<String[]> queue, CountDownLatch latch) {
        String localIp = NetworkUtils.getLocalIPAddress(activity);
        // start check
        String[] ipArr = localIp.split("[.]");
        if (ipArr.length != 4) {
            // stop waiting
            latch.countDown();
            return;
        }
        // parse ip address
        int third = Integer.parseInt(ipArr[2]);
        int forth = Integer.parseInt(ipArr[3]);
        // get zones
        int[] zones = {third, third - 1, third + 1};
        if (third == 0) {
            zones = new int[]{0, 1, 2};
        } else if (third == 255) {
            zones = new int[]{255, 254, 253};
        }
        // submit
        String prefix = ipArr[0] + '.' + ipArr[1] + '.';
        for (int i = 0; i < zones.length; i++) {
            String header = prefix + zones[i] + '.';
            submitWithAnotherThread(queue, latch, header, localIp);
        }
    }

    // submit with another thread
    private void submitWithAnotherThread(Queue<String[]> queue, CountDownLatch latch, String header, String self) {
        // use another thread to submit task
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i <= 255&&isRunning.get(); i++) {
                    String ip = header + String.valueOf(i);
                    // don't check self
                    if (!ip.equals(self)) scanner.check(ip, queue, latch);
                }
            }
        }.start();
    }


    // save info and handle bar
    private void connected(String[] ip) {
        if (ip != null) {
            saveAndFlushInfo(true, ip);
            isConnected.compareAndSet(false, true);
            activity.runOnUiThread(() -> {
                // color
                view.setBackgroundColor(activity.getColor(R.color.status_connected));
                // text
                String devName = ip[1];
                String text = activity.getString(R.string.connected_status);
                text = String.format(text, devName);
                view.setText(text);
            });
        }
    }

    // save info and handle bar
    private void disconnected() {
        // save to preferences and flush info window
        saveAndFlushInfo(false, null);
        // set status
        isConnected.compareAndSet(true, false);
        activity.runOnUiThread(() -> {
            // color
            view.setBackgroundColor(activity.getColor(R.color.status_disconnected));
            // text
            view.setText(activity.getString(R.string.default_status));
        });
    }

    // save and flush info
    private void saveAndFlushInfo(boolean success, String[] ip) {
        saveInfo(success, ip);
        flushInfo(success);
    }

    // flush info window
    private void flushInfo(boolean success) {
        activity.runOnUiThread(() -> {
            String name = "none";
            String address = "none";
            String status = "disconnected";
            int statusColor = activity.getColor(R.color.status_disconnected);
            // check status
            if (success) {
                name = ConnectInfoUtils.getName(activity);
                address = ConnectInfoUtils.getIp(activity);
                status = "connected";
                statusColor = activity.getColor(R.color.status_connected);
            }
            // get view
            TextView nameView = activity.findViewById(R.id.info_name);
            TextView addressView = activity.findViewById(R.id.info_address);
            TextView statusView = activity.findViewById(R.id.info_status);
            // update view
            nameView.setText(name);
            addressView.setText(address);
            statusView.setText(status);
            statusView.setTextColor(statusColor);
        });

    }

    // save info to preferences
    private void saveInfo(boolean success, String[] ip) {
        if (success && ip != null) {
            ConnectInfoUtils.putSign(activity, true);
            ConnectInfoUtils.putIp(activity, ip[0]);
            ConnectInfoUtils.putName(activity, ip[1]);
        } else {
            ConnectInfoUtils.putSign(activity, false);
        }
    }
}
