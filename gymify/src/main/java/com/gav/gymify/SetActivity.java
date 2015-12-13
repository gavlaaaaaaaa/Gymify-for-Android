package com.gav.gymify;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.Notification;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;

import com.gav.gymify.adapter.CardAdapter;
import com.gav.sqlhelper.DatabaseHelper;
import com.gav.sqlmodel.Exercise;
import com.gav.sqlmodel.Set;

public class SetActivity extends Activity {
	
	private DatabaseHelper db;
	private ListView listView;
	private CardAdapter adapter;
	private int exercise_id;
	private Exercise currExercise;
	Notification.Builder mBuilder;

	AlertDialog alertDialog;
	CountDownTimer cdt;
	private Exercise next_exercise;
	MediaPlayer mp;
	ArrayList<Set> sets;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set);

		mBuilder = new Notification.Builder(this);
		//media player for the next set sound
		//mp = MediaPlayer.create(this, R.raw.finished);
		
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
		sets = (ArrayList<Set>) db.getSetsByExercise(exercise_id);
		
		this.getActionBar().setTitle(currExercise.getName());
		
		//initialise the card adapter and set the maximum number of sets (so we know when we have finished this exercise and should start the next)
		adapter = new CardAdapter(this, R.layout.card_row, new ArrayList<Set>(), sets, exercise_id);
		adapter.setMaxSetNo(currExercise.getNoSets());
		
		listView = (ListView) findViewById(R.id.set_list);
		listView.setAdapter(adapter);
		adapter.clear();
		//add the initial set to the screen
		addSet();
		
		db.closeDB();
	}
	
	public void closeKeyboard(){
		InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE); 

		inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                   InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.set, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_settings:
			return true;
		case R.id.action_delete:
			return true;	
		//when using action bar back button - simulate onBackPressed() for animations
		case android.R.id.home:
            onBackPressed();
            return true;
        default:
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_set, container,
					false);
			return rootView;
		}
	}
	
	//adds a blank set card to the screen
	public void addSet(){
		adapter.add(new Set());
		adapter.notifyDataSetChanged();
	}
	
	//create a dialog box to show a countdown timer of 60 seconds until the next set
	//OR next exercise (if we have done all sets for this exercise) should begin.
	//if next exercise then display the previous set data for this next exercise
	// play sound on finish of timer and vibrate.
	public void startTimer(final int curr_set_no, final int max_set_no){
		this.closeKeyboard();
		/*LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.next_exercise_alert, (ViewGroup) findViewById(R.id.layout_root));
		TextView tv = (TextView) layout.findViewById(R.id.next_exercise);
		
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setView(layout);
		alertDialog.setMessage("01:00");
		
		if(curr_set_no >= max_set_no){
			if(next_exercise == null){
				end();
			}
			else{
				sets = (ArrayList<Set>)db.getSetsByExercise(next_exercise.getId());
				alertDialog.setTitle("Next Exercise " + next_exercise.getName() + " in...");
				alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Do One More Set" , new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();
						cdt.cancel();
					}
				});
				
				if(!sets.isEmpty()){
					tv.setText("Last Logs:\nSet 1: "+sets.get(sets.size()-3).getWeight()+"kg	- " + sets.get(sets.size()-3).getNoReps() + " reps\n"
							+ "Set 2: "+sets.get(sets.size()-2).getWeight()+"kg	- " + sets.get(sets.size()-2).getNoReps() + " reps\n"
							+ "Set 3: "+sets.get(sets.size()-1).getWeight()+"kg	- " + sets.get(sets.size()-1).getNoReps() + " reps\n");
					
				}
				else{
					tv.setText("No previous set data for this exercise!");
				}
			}
		}
		else{
			
			alertDialog.setTitle("Next Set in...");  
			alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Skip Break", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					cdt.cancel();
					alertDialog.dismiss();

				}
			});
			
		}
		
		alertDialog.setCanceledOnTouchOutside(false);
		
		alertDialog.show();
		
		// countdown timer used to display tick countdown on screen - vibrates and plays sound on finish to alert user to start next set/exercise
		cdt = new CountDownTimer(60000,1000){
			@Override
		    public void onTick(long millisUntilFinished) {
		       alertDialog.setMessage("00:"+ (millisUntilFinished/1000));
		    }

		    @Override
		    public void onFinish() {
		    	alertDialog.dismiss();
		    	mBuilder.setVibrate(new long[] { 3000 });
	    		NotificationManager mNotifyMgr = 
	    		        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

	    		mNotifyMgr.notify(001, mBuilder.build());
	    		//mp.start();
		    	if(curr_set_no == max_set_no){
		    		end();
		    	}

		    }
		}.start();
		*/
	}
	
	//called to notify that all sets have been finished so returns that the activity has successfully finished to the ExerciseActivity.
	public void end(){
		Intent returnIntent = new Intent();
		setResult(Activity.RESULT_OK, returnIntent);
		finish();
	}
	
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    //ensure that the dialog and timer are closed and finished if the user presses the back button
	    //prevents the timer continuing and notifying later out of context
	    if(alertDialog != null && cdt != null){
	    	alertDialog.dismiss();
	    	cdt.cancel();
	    }
	    
	    overridePendingTransition(R.anim.slide_in_left, android.R.anim.fade_out);   
	}
}
