package com.example.dashboard;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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


//Purpose: Mock up prototype of simulated dashboard for Capstone project.
//Handles the random animation of visual gauges (speedometer, RPM, Gas, Heart rate)
public class MainActivity extends AppCompatActivity {

    ImageView m_speedImage;
    ImageView m_heartImage;
    
    TextView m_heartRate;
    ProgressBar m_speedBar;
    ProgressBar m_gasBar;

    RotateAnimation m_speedAnim;
    ScaleAnimation m_heartAnim;
    
    ObjectAnimator m_RPMAnimation;
    ObjectAnimator m_gasAnimation;

    float m_currentNeedlePos = 0;
    int m_currentGasProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);


        //get reference to buttons and gauges.
        Button randomButton = (Button)findViewById(R.id.random_speed);

        m_speedBar = (ProgressBar)findViewById(R.id.rpm);
        m_gasBar = (ProgressBar)findViewById(R.id.GasBar);

        m_speedImage = (ImageView)findViewById(R.id.line);
        m_heartImage = (ImageView)findViewById(R.id.heartImage);
        m_heartRate = (TextView)findViewById(R.id.bpm);

        //Set initial animations
        SetNewSpeedAnimation(0.0f,-180.0f);
        SetNewHeartAnimation(75);


        //Progress bar animations do not have their own animation class in Marshmallow
        //We must instead use the property animator.
        m_gasAnimation = ObjectAnimator.ofInt(m_speedBar, "progress", 0, 100);
        m_gasAnimation.setInterpolator(new LinearInterpolator());
        m_gasAnimation.setDuration(1500);
        m_gasAnimation.start();


        randomButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view)
            {
                if(true)
                {
                    Random r = new Random();
                    float nextpos = (float)(r.nextInt(180) - 0);
                    SetNewSpeedAnimation(-m_currentNeedlePos, -nextpos);
                    m_currentNeedlePos = nextpos;

                }
                ////////////////////////////////////////////////////
                Random r = new Random();
                int bpm = (r.nextInt(100) + 45);

                m_heartRate.setText(Integer.toString(bpm));
                SetNewHeartAnimation(bpm);
                //////////////////////////////////////////////////////

                SetNewRpmAnimation();
                SetNewGasAnimation();
            }
        });
        
    }



    public void SetNewSpeedAnimation(float start, float end){

        m_speedImage.clearAnimation();
        m_speedAnim = new RotateAnimation(start ,end, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        m_speedAnim.setInterpolator(new LinearInterpolator());

        m_speedAnim.setRepeatCount(0);
        m_speedAnim.setFillAfter(true);
        m_speedAnim.setDuration(1000);
        m_speedImage.setAnimation(m_speedAnim);

    }

    public void SetNewHeartAnimation(float bpm){

        int rate = (int)((60 / bpm * 1000) /4);

        m_heartImage.clearAnimation();
        m_heartAnim = new ScaleAnimation(1.0f,1.2f,1.0f,1.2f, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        m_heartAnim.setInterpolator(new LinearInterpolator());

        m_heartAnim.setRepeatCount(Animation.INFINITE);
        m_heartAnim.setRepeatMode(Animation.REVERSE);
        m_heartAnim.setDuration(rate);
        m_heartImage.setAnimation(m_heartAnim);

    }

    private void SetNewRpmAnimation(){

        Random r = new Random();
        int progress = (r.nextInt(100));

        m_RPMAnimation = ObjectAnimator.ofInt(m_speedBar, "progress", 0, progress);
        m_RPMAnimation.setInterpolator(new LinearInterpolator());
        m_RPMAnimation.setRepeatCount(1);
        m_RPMAnimation.setRepeatMode(ValueAnimator.REVERSE);
        m_RPMAnimation.setDuration(500);
        m_RPMAnimation.start();

    }


    private void SetNewGasAnimation(){

        Random r = new Random();
        int progress = (r.nextInt(100));

        m_gasAnimation = ObjectAnimator.ofInt(m_gasBar, "progress", m_currentGasProgress, progress);
        m_gasAnimation.setInterpolator(new LinearInterpolator());
        m_gasAnimation.setDuration(1000);
        m_gasAnimation.start();

        m_currentGasProgress = progress;
    }

    //Cretes a menu bar options menu
    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    //     getMenuInflater().inflate(R.menu.menu_main, menu);
    //     return true;
    // }

    // //Hangles Item bar clicks
    // @Override
    // public boolean onOptionsItemSelected(MenuItem item) {
    //     int id = item.getItemId();

    //     if(id == R.id.action_settings){
    //         return true;
    //     }
    //     if(id == R.id.action_about){
    //         Intent intent = new Intent(this, AnimationActivity.class);
    //         startActivity(intent);
    //         return true;
    //     }
    //     return super.onOptionsItemSelected(item);
    // }


}
