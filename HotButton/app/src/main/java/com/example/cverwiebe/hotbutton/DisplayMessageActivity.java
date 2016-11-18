package com.example.cverwiebe.hotbutton;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.logging.Level;
import java.util.logging.Logger;


public class DisplayMessageActivity extends ActionBarActivity implements SensorEventListener {
    private static final String TAG = "DisplayMessageActivity";
    private static final long TIME_THRESHOLD_NS = 0;
//   private static final double ACCELARATION_THRESHOLD = 5;
    private static final double ACCELARATION_THRESHOLD = 0;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private double vx = 0;
    private double vy = 0;
    private double vz = 0;
    private long mLastTime = 0;
    private double accel = 0;
    private double oldx =0;
    private double oldy =0;
    private double oldz =0;
    private TextView textView;
    private SensorEvent lastEvent;
    private int counter = 0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL)) {
            if (Log.isLoggable(TAG, Log.INFO)) {
                Log.i(TAG, "Successfully registered for the sensor updates");
            }
        }
        Intent intent = getIntent();
        String message = intent.getStringExtra(ShowTheMaster.EXTRA_MESSAGE);
        textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);
        setContentView(textView);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL)) {
            if (Log.isLoggable(TAG, Log.INFO)) {
                Log.i(TAG, "Successfully registered for the sensor updates");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        if (Log.isLoggable(TAG, Log.INFO)) {
            Log.i(TAG, "Unregistered for sensor events");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.timestamp - mLastTime > TIME_THRESHOLD_NS) {
            if (directionChangeDetected(event, lastEvent)) counter++;
            textView.setText("x:" + event.values[0] + "\n");
            textView.append("y:" + event.values[1] + "\n");
            textView.append("z:" + event.values[2] + "\n");
            textView.append("counti:" + counter + "\n");
            if (mLastTime > 0) {
                double dt = 1000000 * (event.timestamp - mLastTime);
                vx = vx + event.values[0] * dt;
            }
  //          textView.append(String.format("nvx: %f \n",vx ));
            textView.append("nvx: " +vx );
            setContentView(textView);
            mLastTime = event.timestamp;
            lastEvent = event;
            oldx = event.values[0];
            oldy = event.values[1];
            oldz = event.values[2];



        }

        // Log.i(TAG, "S" + event.values[0] + event.timestamp);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public boolean directionChangeDetected(SensorEvent event, SensorEvent last) {
        if (lastEvent == null) {
            return false;
        }
        double vlength = Math.sqrt(Math.pow(event.values[0], 2.0) + Math.pow(event.values[1], 2.0) + Math.pow(event.values[2], 2.0));
        //  Log.i(TAG, "l:" + String.valueOf(vlength));
        Log.i(TAG, "test out at " + event.timestamp);
        Log.i(TAG, "before " + last.timestamp);
        Log.i(TAG, "signum n:" + String.valueOf(Math.signum(event.values[0])));
        Log.i(TAG, "signum l:" + String.valueOf(Math.signum(lastEvent.values[0])));
        Log.i(TAG, "signum x:" + String.valueOf(Math.signum(oldx)));
        Log.i(TAG, "signum:" + String.valueOf(Math.signum(event.values[1])));
        Log.i(TAG, "signum:" + String.valueOf(Math.signum(lastEvent.values[1])));
        Log.i(TAG, "signum:" + String.valueOf(Math.signum(event.values[2])));
        Log.i(TAG, "signum:" + String.valueOf(Math.signum(lastEvent.values[2])));

        if (vlength > ACCELARATION_THRESHOLD) {
            //   Log.i(TAG, "l:" + String.valueOf(vlength));
            Log.i(TAG, "test in");

            if (Math.signum(event.values[0]) != Math.signum(oldx) || (Math.signum(event.values[1]) != Math.signum(oldy) || Math.signum(event.values[2]) != Math.signum(oldz))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "DisplayMessage Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.cverwiebe.hotbutton/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "DisplayMessage Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.cverwiebe.hotbutton/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
