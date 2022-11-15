package com.victor2022.remote_controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.victor2022.remote_controller.handlers.ConnectBarHandler;
import com.victor2022.remote_controller.handlers.ControlButtonHandler;
import com.victor2022.remote_controller.utils.ConnectInfoUtils;

public class SettingsActivity extends AppCompatActivity {

    private View connectBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // clear last connect
        ConnectInfoUtils.clearConnectionInfo(this);
        // get connect bar
        connectBar = findViewById(R.id.connect_bar);
        setClickListener();
    }

    private void setClickListener() {
        // set connect bar listener
        View.OnClickListener connectBarListener = (view) -> {
            new ConnectBarHandler(this, view).handle();
        };
        connectBar.setOnClickListener(connectBarListener);
        // set control listener
        setControlClickListener();
    }

    private void setControlClickListener() {
        // power button
        View.OnClickListener powerBtnListener = (view) -> {
            ControlButtonHandler.handlePower(this);
        };
        findViewById(R.id.btn_power).setOnClickListener(powerBtnListener);
    }

}