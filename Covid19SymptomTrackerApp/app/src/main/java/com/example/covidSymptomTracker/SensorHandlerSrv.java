package com.example.covidSymptomTracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

public class SensorHandlerSrv extends Service implements SensorEventListener {
    public SensorHandlerSrv() {
    }

    public static final int mDuration_ = 45; //seconds
    public static final int mFrequency_ = 10; // 10 samples per seconds
    public static final int mReadingRate_ = 1000 * 1000 / mFrequency_;
    public static final int mNumSamples_ = mDuration_ * (mFrequency_ + 2); // Receiving two samples extra

    private SensorManager mSensorManager_;
    private ResultReceiver mResultReceiver_;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager_ = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = mSensorManager_.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager_.registerListener(this, accelerometer, mReadingRate_);
        this.setResultReceiver(intent.getParcelableExtra(Intent.EXTRA_RESULT_RECEIVER));
        return START_NOT_STICKY;
    }

    int index = 0;
    public static final float epsilon = 19;
    private float[] z = new float[mNumSamples_];
    private final float[] diff = new float[mNumSamples_];
    private int peak = 0;
    private float prev;

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (index < mNumSamples_) {
            float curr = (event).values[2];
            if (index > 0) {
                diff[index] = (100 * (curr - prev));
                if ((diff[index - 1] < epsilon) && (diff[index] > epsilon)) {
                    peak = peak + 1;
                }
            } else {
                // When index is 0
                diff[index] = 0;
            }
            prev = curr;
            Log.d("Accelerometer Diff", String.valueOf(diff[index]));
            sendDataToHome(peak);
            index++;
        } else {
            index = 0;
            sendDataToHome((peak * 60) / (2 * mDuration_));
            mSensorManager_.unregisterListener(this);
            mResultReceiver_.send(MainActivity.RESULT_CANCELED, null);
        }
    }

    void sendDataToHome(int data) {
        Bundle bundle = new Bundle();
        bundle.putString("Result", Integer.toString(data));
        mResultReceiver_.send(MainActivity.RESULT_OK, bundle);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        mResultReceiver_.send(MainActivity.RESULT_CANCELED, null);
        mSensorManager_.unregisterListener(this); //
    }

    public void setResultReceiver(ResultReceiver mResultReceiver) {
        this.mResultReceiver_ = mResultReceiver;
    }
}