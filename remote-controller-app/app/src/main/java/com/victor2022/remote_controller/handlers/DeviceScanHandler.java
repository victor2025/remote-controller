package com.victor2022.remote_controller.handlers;

import android.util.Log;

import com.victor2022.remote_controller.utils.HttpUtils;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DeviceScanHandler {

    private static final String DEVICE_PREFIX = "remote-controller";
    private static final String ID_DOMAIN = "/identity/";
    private static ThreadPoolExecutor pool;

    public DeviceScanHandler() {
        checkPool();
    }

    // make sure the thread pool is ready
    private void checkPool(){
        if (pool == null || pool.isTerminated()) {
            this.pool = new ThreadPoolExecutor(4, 6,
                    2, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(100),
                    new ThreadPoolExecutor.CallerRunsPolicy());
        }
    }

    // check specified address
    public void check(String address, Queue<String[]> queue, CountDownLatch latch) {
        // check thread pool
        checkPool();
        // start task
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String url = HttpUtils.HTTP_PREFIX+address+ID_DOMAIN;
                Log.i("DeviceScanner","requesting ip: "+url);
                // get resp
                String resp = HttpUtils.httpGet(url, 200, 1000);
                Log.i("DeviceScanner","ip: "+url+" get response: "+resp);
                if(resp!=null&&resp.startsWith(DEVICE_PREFIX)){
                    int idx = resp.indexOf(':');
                    String name = resp.substring(idx+1);
                    String[] arr = {address, name};
                    queue.offer(arr);
                    // inform caller
                    latch.countDown();
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
