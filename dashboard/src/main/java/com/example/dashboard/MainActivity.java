package com.example.dashboard;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

//Purpose: Mock up prototype of simulated dashboard for Capstone project.
//Handles the random animation of visual gauges (speedometer, RPM, Gas, Heart rate)
public class MainActivity extends AppCompatActivity {

    ImageView m_speedImage;
    ImageView m_heartImage;
    ImageView m_rpmImage;
    TextView m_heartRate;

    RotateAnimation m_speedAnim;
    ScaleAnimation m_heartAnim;

    float m_currentNeedlePos = 0;
    boolean isVisible = true;

    AlertHandler m_alertHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_zenpad);

        //Collect references to art
        m_speedImage = (ImageView) findViewById(R.id.Line);
        m_heartImage = (ImageView) findViewById(R.id.heartImage);
        m_heartRate = (TextView) findViewById(R.id.bpm);
        m_rpmImage = (ImageView) findViewById(R.id.rpmLine);

        // Register local broadcast receiver
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        //ALERTS.
        //---------------------------------------------
        m_alertHandler = (AlertHandler)getFragmentManager().findFragmentById(R.id.alertFragment);


        //Initalize transition animations
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(0, 0);
        ft.hide(getFragmentManager().findFragmentById(R.id.alertFragment));
        ft.commit();
        //--------------------------------------------

        //get reference to buttons.
        Button scenerio1 = (Button) findViewById(R.id.random_speed);

        //Set initial animations
        SetNewHeartAnimation(75);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //Modulate needle rotation up and down for speed and rpm
                        float nextpos = 0;
                        if(m_currentNeedlePos == -110)
                            nextpos = 60;
                        else
                        nextpos = -110;

                        SetNewNeedleAnimation(m_speedImage, m_speedAnim, -m_currentNeedlePos, -nextpos, 1500);
                        SetNewNeedleAnimation(m_rpmImage, m_speedAnim, -m_currentNeedlePos, -nextpos, 500);
                        m_currentNeedlePos = nextpos;
                    }
                });
            }
        },0,3000);



        scenerio1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(isVisible)
                {
                    HideAlert();
                }
                else
                {
                    ShowAlert();
                }
                isVisible = !isVisible;
            }
        });
    }
    public void SetNewNeedleAnimation(ImageView image, RotateAnimation animation, float start, float end, int duration) {

        image.clearAnimation();
        animation = new RotateAnimation(start, end, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());

        animation.setRepeatCount(0);
        animation.setFillAfter(true);
        animation.setDuration(duration);
        image.setAnimation(animation);

    }

    public void SetNewHeartAnimation(float bpm) {

        int rate = (int) ((60 / bpm * 1000) / 4);

        m_heartImage.clearAnimation();
        m_heartAnim = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        m_heartAnim.setInterpolator(new LinearInterpolator());

        m_heartAnim.setRepeatCount(Animation.INFINITE);
        m_heartAnim.setRepeatMode(Animation.REVERSE);
        m_heartAnim.setDuration(rate);
        m_heartImage.setAnimation(m_heartAnim);

    }


    private void ShowAlert(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in,R.anim.slide_out);
        ft.show(getFragmentManager().findFragmentById(R.id.alertFragment));
        ft.commit();
    }

    private void HideAlert(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in,R.anim.slide_out);
        ft.hide(getFragmentManager().findFragmentById(R.id.alertFragment));
        ft.commit();
    }

    // Message Receive Service
    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            double d = Double.parseDouble(message);
            int messageAsInt = (int) d;

            m_heartRate.setText(Integer.toString(messageAsInt));
            SetNewHeartAnimation(messageAsInt);
        }
    }
}
