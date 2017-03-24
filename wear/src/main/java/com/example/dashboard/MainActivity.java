package com.example.dashboard;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends WearableActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        SensorEventListener {

    public static String TAG = "WearActivity";

    Node mNode; // the connected device to send the message to
    GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;

    SensorManager mSensorManager;
    Sensor mHeartRateSensor;
    int mSensorRateOfFire = 5;

    Vibrator mVibrator;

    static private final int MY_PERMISSIONS_REQUEST_BODY_SENSORS = 1;
    static private final int MY_PERMISSIONS_REQUEST_VIBRATE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setAmbientEnabled();

        // Connect the GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Sensor Management
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Register local broadcast receiver
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        // Request BODY_SENSORS permissions
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BODY_SENSORS},
                    MY_PERMISSIONS_REQUEST_BODY_SENSORS);
        } else {
            // Already have permissions
            mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
            mSensorManager.registerListener(this, mHeartRateSensor, mSensorRateOfFire);
        }

        // Request VIBRATOR_SERVICE permissions
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.VIBRATE},
                    MY_PERMISSIONS_REQUEST_VIBRATE);
        } else {
            // Vibrator
            mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
        mSensorManager.registerListener(this, mHeartRateSensor, mSensorRateOfFire);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!mResolvingError) {
            mGoogleApiClient.disconnect();
        }
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mResolvingError) {
            mGoogleApiClient.disconnect();
        }
        mSensorManager.unregisterListener(this);
    }

    /**
     * Resolve the node = the connected device to send the message to
     */
    private void resolveNode() {

        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient)
                .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                        for (Node node : nodes.getNodes()) {
                            mNode = node;
                        }
                    }
                });
    }

    @Override
    public void onConnected(Bundle bundle) {
        resolveNode();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_BODY_SENSORS: {
                // if request is cancelled, result arrays are empty
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("SUCCESS", "YES");
                    mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
                    mSensorManager.registerListener(this, mHeartRateSensor, mSensorRateOfFire);
                }
            }
            case MY_PERMISSIONS_REQUEST_VIBRATE: {
                // if request is cancelled, result arrays are empty
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("SUCCESS", "YES");
                    // Vibrator
                    mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                }
            }
        }
    }

    /**
     * Send message to mobile handheld
     */
    private void sendMessage(String Key) {

        if (mNode != null && mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Wearable.MessageApi.sendMessage(mGoogleApiClient, mNode.getId(), Key, null).setResultCallback(

                    // If send fails, return an error code
                    new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {

                            if (!sendMessageResult.getStatus().isSuccess()) {
                                Log.e(TAG, "Failed to send message with status code: "
                                        + sendMessageResult.getStatus().getStatusCode());
                            }
                        }
                    }
            );
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            sendMessage(String.valueOf(event.values[0]));
            TextView textView = (TextView) findViewById(R.id.hr_text);
            textView.setText("Heart Rate: " + String.valueOf(event.values[0]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Message Receive Service
    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            long[] pattern = {0, 1000, 1000};

            switch(message) {
                case "start": {
                    Log.d("TAG", "started");
                    // Haptic Response
                    if (mVibrator.hasVibrator()) {
                        mVibrator.vibrate(pattern, 1);
                    } else {
                        Log.d("Tag", "No accessible Vibrator");
                    }
                    break;
                }
                case "stop": {
                    Log.d("TAG", "stopped");
                    mVibrator.cancel();
                    break;
                }
            }
        }
    }
}