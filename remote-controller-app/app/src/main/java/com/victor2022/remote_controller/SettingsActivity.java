package com.victor2022.remote_controller;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.victor2022.remote_controller.handlers.ConnectBarHandler;
import com.victor2022.remote_controller.handlers.ControlPanelHandler;
import com.victor2022.remote_controller.handlers.HeartBeatHandler;

public class SettingsActivity extends AppCompatActivity {

    private HeartBeatHandler heartBeatHandler = null;
    private ControlPanelHandler controlPanelHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // set click listener
        setClickListener();
        // create controlPanelHandler
        this.controlPanelHandler = new ControlPanelHandler(this);
        // start heart beat
        this.heartBeatHandler = new HeartBeatHandler();
        // check if the previous connection is still valid
        this.heartBeatHandler.checkPreviousConnection(this);
        // start heart beat with period
        this.heartBeatHandler.startHeartBeat(this);
    }

    private void setClickListener() {
        // set connect bar listener
        setConnectBarListener();
        // set control listener
        setControlClickListener();
    }

    private void setConnectBarListener(){
        // connection bar
        View connectBar = findViewById(R.id.connect_bar);
        View.OnClickListener connectBarListener = (view) -> {
            new ConnectBarHandler(this, view).handle();
        };
        connectBar.setOnClickListener(connectBarListener);
    }

    private void setControlClickListener() {
        // control buttons
        findViewById(R.id.btn_power)
                .setOnClickListener((view)->controlPanelHandler.handlePower());
        findViewById(R.id.btn_mode)
                .setOnClickListener((view)->controlPanelHandler.handleMode());
        findViewById(R.id.btn_up)
                .setOnClickListener((view)->controlPanelHandler.handleUp());
        findViewById(R.id.btn_down)
                .setOnClickListener((view)->controlPanelHandler.handleDown());
    }

}