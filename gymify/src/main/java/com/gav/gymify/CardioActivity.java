package com.gav.gymify;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.os.SystemClock;
import android.text.Html;
import android.text.format.Time;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gav.sqlhelper.DatabaseHelper;
import com.gav.sqlmodel.Exercise;
import com.gav.sqlmodel.Set;

public class CardioActivity extends Activity implements SensorEventListener, View.OnClickListener{


    private SensorManager mSensorManager;
    private Sensor mStepDetectorSensor;
    private TextView detectorTV;
    private Chronometer chronometer;
    private TextView circleLayout;
    private TextView lastSet;
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
    private Set last_set;
    private boolean chronometerStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardio);

        detectorTV = (TextView)findViewById(R.id.detector);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        circleLayout = (TextView) findViewById(R.id.detector);
        lastSet = (TextView) findViewById(R.id.lastset);
        findViewById(R.id.startBtn).setOnClickListener(this);
        findViewById(R.id.pauseBtn).setOnClickListener(this);
        findViewById(R.id.finishBtn).setOnClickListener(this);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

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

        last_set = db.getLastSetForExercise(exercise_id);
        if(last_set != null){
            lastSet.setText(Html.fromHtml("<u>Last "+currExercise.getName()+"</u><br>Distance: "+last_set.getDistance()+"\t"+"Time: "+last_set.getTimespentAsString()));
        }
        else{
            lastSet.setText("No Previous data for this exercise");
        }

        //set page title to match current exercise name
        this.getActionBar().setTitle(currExercise.getName());

        finishDialogBuilder = new AlertDialog.Builder(this);

        finishDialogBuilder.setTitle("Finish Exercise?");
        if(next_exercise == null){
            finishDialogBuilder.setMessage("This was your last exercise. Great work!");
        }
        else{
            //TODO: add previous exercise info here
            finishDialogBuilder.setMessage("Next Exercise is: " + next_exercise.getName());
        }
        finishDialogBuilder.setCancelable(true);
        finishDialogBuilder.setNeutralButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CardioActivity.super.onBackPressed();
                CardioActivity.this.overridePendingTransition(R.anim.slide_in_left, android.R.anim.fade_out);

            }
        });

        finishDialogBuilder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Time now = new Time();
                        now.setToNow();
                        long millis = SystemClock.elapsedRealtime() - chronometer.getBase();
                        Set cardioSet = new Set(now.format("%H:%M - %m-%d-%Y"), getDistanceRun(steps),millis,steps);
                        int setId = (int)db.createSet(cardioSet);
                        cardioSet.setId(setId);
                        db.createExerciseSet(exercise_id, setId);
                        dialog.cancel();
                        end();
                    }
                });

        finishDialogBuilder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        chronometer.start();
                    }
                });

        finishDialog = finishDialogBuilder.create();

        growAnimX = ObjectAnimator.ofFloat(circleLayout, "scaleX", 1.1f);
        growAnimY = ObjectAnimator.ofFloat(circleLayout, "scaleY", 1.1f);
        growAnimX.setDuration(600);
        growAnimY.setDuration(600);
        scaleCircle.play(growAnimX).with(growAnimY);



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
        switch (v.getId()) {
            case R.id.startBtn:
                chronometerStarted = true;
                chronometer.setBase(SystemClock.elapsedRealtime() + exerciseTime);
                chronometer.start();
                break;
            case R.id.pauseBtn:
                exerciseTime = chronometer.getBase() - SystemClock.elapsedRealtime();
                chronometer.stop();
                break;
            case R.id.finishBtn:
                chronometer.stop();
                if (!chronometerStarted && steps == 0) {
                    super.onBackPressed();
                    this.overridePendingTransition(R.anim.slide_in_left, android.R.anim.fade_out);
                } else {
                    finishDialog.show();
                }
        }
    }

    @Override
    public void onBackPressed(){
        if(!chronometerStarted && steps == 0){
            super.onBackPressed();
            this.overridePendingTransition(R.anim.slide_in_left, android.R.anim.fade_out);
        }
        else {
            finishDialog.show();
        }
    }

    public void end(){
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public float getDistanceRun(long steps){
        //TODO: Add gender to change average step length
        //Distance = number of steps x average step length / 100,000
        float distance = (float)(steps*75)/(float)100000;
        return distance;
    }
}
