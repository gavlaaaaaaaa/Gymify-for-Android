package com.gav.gymify;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gav.sqlhelper.DatabaseHelper;
import com.gav.sqlmodel.Exercise;

public class CardioActivity extends Activity implements SensorEventListener, View.OnClickListener{


    private SensorManager mSensorManager;
    private Sensor mStepDetectorSensor;
    private TextView detectorTV;
    private Chronometer chronometer;
    private TextView circleLayout;
    private long steps = 0;
    private long exerciseTime = 0;
    private AlertDialog.Builder finishDialogBuilder;
    private AlertDialog finishDialog;
    private ObjectAnimator growAnimX;
    private ObjectAnimator growAnimY;
    private AnimatorSet scaleCircle = new AnimatorSet();
    private Exercise currExercise;
    private Exercise next_exercise;
    private DatabaseHelper db;
    private int exercise_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardio);

        detectorTV = (TextView)findViewById(R.id.detector);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        circleLayout = (TextView) findViewById(R.id.detector);
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
                        //TODO: store results here
                        dialog.cancel();
                        CardioActivity.super.onBackPressed();
                        CardioActivity.this.overridePendingTransition(R.anim.slide_in_left, android.R.anim.fade_out);
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

        growAnimX = ObjectAnimator.ofFloat(circleLayout, "scaleX", 1.1f);
        growAnimY = ObjectAnimator.ofFloat(circleLayout, "scaleY", 1.1f);
        growAnimX.setDuration(900);
        growAnimY.setDuration(900);
        scaleCircle.play(growAnimX).with(growAnimY);

        //get a db instance and obtain the current and next exercise
        db = new DatabaseHelper(getApplicationContext());
        exercise_id = getIntent().getIntExtra("exercise_id", 0);
        int id = getIntent().getIntExtra("next_exercise_id", -1);
        //make sure there is a next exercise - otherwise set it to null
        if(id >=0){
            next_exercise = db.getExercise(getIntent().getIntExtra("next_exercise_id", -1));
        }
        else{
            next_exercise = null;
        }
        currExercise = db.getExercise(exercise_id);

        //set page title to match current exercise name
        this.getActionBar().setTitle(currExercise.getName());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            //when using action bar back button - simulate onBackPressed() for animations
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
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
            scaleCircle.start();
            detectorTV.setText("Steps\n" + steps);
            //scaleCircle.end();
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
