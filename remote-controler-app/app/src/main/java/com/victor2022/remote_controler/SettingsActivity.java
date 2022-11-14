package com.victor2022.remote_controler;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.victor2022.remote_controler.handlers.ConnectBarHandler;
import com.victor2022.remote_controler.utils.NetworkUtils;

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
        // get connect bar
        connectBar = findViewById(R.id.connect_bar);
        setClickListener();
    }

    private void setClickListener(){
        // set connect bar listener
        View.OnClickListener connectBarListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ConnectBarHandler(getApplicationContext(), view).handle();
            }
        };
        connectBar.setOnClickListener(connectBarListener);
    }

}