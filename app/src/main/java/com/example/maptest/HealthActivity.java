package com.example.maptest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class HealthActivity extends AppCompatActivity {
    private Queue<Integer> heartRateData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);
        heartRateData = new LinkedList<Integer>();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // get bpm
                        TextView textView = (TextView) findViewById(R.id.tv_bpm);
                        int rand = new Random().nextInt(100);
                        textView.setText(String.valueOf(rand));
                        heartRateData.add(rand);
                        if(heartRateData.size() > 100) {
                            heartRateData.remove();
                        }

                        // get avg bpm
                        int avgHeartRate = 0;
                        for (Integer i : heartRateData) {
                            avgHeartRate += i;
                        }
                        avgHeartRate /= heartRateData.size();
                        TextView avgView = (TextView) findViewById(R.id.tv_avg);
                        avgView.setText(String.valueOf(avgHeartRate));
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
}
