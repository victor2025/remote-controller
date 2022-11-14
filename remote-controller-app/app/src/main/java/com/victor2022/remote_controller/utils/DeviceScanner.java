package com.victor2022.remote_controller.utils;

import android.util.Log;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DeviceScanner {

    private static final String ID_DOMAIN = "/identity/";
    private ThreadPoolExecutor pool;
    private boolean completed = false;

    public DeviceScanner() {
        if (pool == null || pool.isShutdown()) {
            this.pool = new ThreadPoolExecutor(2, 4,
                    2, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(255),
                    new ThreadPoolExecutor.DiscardOldestPolicy());
        }
    }

    public void check(String address, SynchronousQueue<String[]> queue) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String url = address+ID_DOMAIN;
                Log.i("DeviceScanner","requesting ip: "+url);
                // get resp
                String resp = HttpUtils.httpGet(url, null);
                Log.i("DeviceScanner","ip: "+url+" get response: "+resp);
                if(resp!=null&&resp.startsWith("remote-controller")){
                    int idx = resp.indexOf(':');
                    String name = resp.substring(idx+1);
                    String[] arr = {address, name};
                    try {
                        queue.put(arr);
                    } catch (InterruptedException e) {}
                }
            }
        };
        pool.submit(runnable);
    }

    public void terminate() {
        if (pool != null) {
            pool.shutdownNow();
        }
    }

}
