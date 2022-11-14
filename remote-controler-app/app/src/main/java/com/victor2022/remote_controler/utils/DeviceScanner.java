package com.victor2022.remote_controler.utils;

import android.app.Activity;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DeviceScanner {

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

    public void check(String address, List<String[]> list) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println(address);
                if (address != null) {
                    list.add(new String[]{address, "test_name"});
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
