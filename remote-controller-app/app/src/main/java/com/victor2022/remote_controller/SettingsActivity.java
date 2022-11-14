package com.victor2022.remote_controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.victor2022.remote_controller.handlers.ConnectBarHandler;

public class SettingsActivity extends AppCompatActivity {

    private View connectBar = null;
    private Activity self = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        this.self = this;
        // get connect bar
        connectBar = findViewById(R.id.connect_bar);
        setClickListener();
    }

    private void setClickListener(){
        // set connect bar listener
        View.OnClickListener connectBarListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ConnectBarHandler(self, view).handle();
            }
        };
        connectBar.setOnClickListener(connectBarListener);
    }

}