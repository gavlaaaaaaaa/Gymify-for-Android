package com.gav.gymify;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CardioActivity extends Activity implements SensorEventListener, View.OnClickListener{


    private SensorManager mSensorManager;
    private Sensor mStepDetectorSensor;
    private TextView detectorTV;
    private Chronometer chronometer;
    private RelativeLayout circleLayout;
    private long steps = 0;
    private long exerciseTime = 0;
    private  AlertDialog.Builder finishDialogBuilder;
    private AlertDialog finishDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardio);

        detectorTV = (TextView)findViewById(R.id.detector);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        //circleLayout = (RelativeLayout) findViewById(R.id.circleLayout);
        findViewById(R.id.startBtn).setOnClickListener(this);
        findViewById(R.id.pauseBtn).setOnClickListener(this);
        findViewById(R.id.finishBtn).setOnClickListener(this);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        finishDialogBuilder = new AlertDialog.Builder(this);
        finishDialogBuilder.setMessage("Finish Exercise?");
        finishDialogBuilder.setCancelable(true);

        finishDialogBuilder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        CardioActivity.super.onBackPressed();
                    }
                });

        finishDialogBuilder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        finishDialog = finishDialogBuilder.create();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float[] values = event.values;
        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }


        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            steps++;
            detectorTV.setText("Steps\n" + steps);
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this, mStepDetectorSensor);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.startBtn:
                chronometer.setBase(SystemClock.elapsedRealtime() + exerciseTime);
                chronometer.start();
                break;
            case R.id.pauseBtn:
                exerciseTime = chronometer.getBase() - SystemClock.elapsedRealtime();
                chronometer.stop();
                break;
            case R.id.finishBtn:
                finishDialog.show();
        }
    }

    @Override
    public void onBackPressed(){
        finishDialog.show();
    }
}
