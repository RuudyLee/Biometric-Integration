package com.example.maptest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class HealthActivity extends AppCompatActivity {
    private Queue<Integer> heartRateData;
    TextView tv_BPM;
    TextView tv_AVG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);

        heartRateData = new LinkedList<Integer>();
        tv_BPM = (TextView) findViewById(R.id.tv_bpm);
        tv_AVG = (TextView) findViewById(R.id.tv_avg);

        // Register local broadcast receiver
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (heartRateData.size() > 0) {
                            if (heartRateData.size() > 100) {
                                heartRateData.remove();
                            }

                            // get avg bpm
                            int avgHeartRate = 0;
                            for (Integer i : heartRateData) {
                                avgHeartRate += i;
                            }
                            avgHeartRate /= heartRateData.size();
                            tv_AVG.setText(String.valueOf(avgHeartRate));
                        }
                    }
                });

            }
        }, 0, 1000);
    }

    /////////////
    // BUTTONS //
    /////////////

    public void vehiclePressed(View view) {
        Intent intent = new Intent(this, VehicleActivity.class);
        startActivity(intent);
    }

    public void mapsPressed(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void mediaPressed(View view) {
        Intent intent = new Intent(this, MediaActivity.class);
        startActivity(intent);
    }

    public void infoPressed(View view) {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    public void settingsPressed(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            double d = Double.parseDouble(message);
            int messageAsInt = (int) d;

            tv_BPM.setText(String.valueOf(messageAsInt));
            heartRateData.add(messageAsInt);
        }
    }
}
