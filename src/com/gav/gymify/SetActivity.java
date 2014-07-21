package com.gav.gymify;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import com.gav.sqlhelper.DatabaseHelper;
import com.gav.sqlmodel.Exercise;
import com.gav.sqlmodel.Set;

public class SetActivity extends Activity {
	
	private DatabaseHelper db;
	private ListView listView;
	private CardAdapter adapter;
	private int exercise_id;
	private Exercise currExercise;
	NotificationCompat.Builder mBuilder =
		    new NotificationCompat.Builder(this);
	AlertDialog alertDialog;
	CountDownTimer cdt;
	private int next_pos;
	MediaPlayer mp;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		mp = MediaPlayer.create(this, R.raw.finished);
		exercise_id = getIntent().getIntExtra("exercise_id", 0);
		next_pos = getIntent().getIntExtra("next_pos", 0);
		db = new DatabaseHelper(getApplicationContext());
		currExercise = db.getExercise(exercise_id);
		ArrayList<Set> sets = (ArrayList<Set>) db.getSetsByExercise(exercise_id);
		
		this.getActionBar().setTitle(currExercise.getName());
		
		adapter = new CardAdapter(this, R.layout.card_row, new ArrayList<Set>(), sets, exercise_id);
		adapter.setMaxSetNo(currExercise.getNoSets());
		listView = (ListView) findViewById(R.id.set_list);
		listView.setAdapter(adapter);
		adapter.clear();
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
	
	
	public void addSet(){
		adapter.add(new Set());
		adapter.notifyDataSetChanged();
		
	}
	
	public void startTimer(final int curr_set_no, final int max_set_no){
		this.closeKeyboard();
		
		alertDialog = new AlertDialog.Builder(this).create();
		
		alertDialog.setMessage("01:00");
		
		 // defaults to false
		if(curr_set_no >= max_set_no){
			if(next_pos == 0){
				end();
			}
			else{
				alertDialog.setTitle("Next Exercise in...");
				alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Do One More Set" , new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();
						cdt.cancel();
					}
				});
			}
		}
		else{
			
			alertDialog.setTitle("Next Set in...");  
			alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Skip Break", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					alertDialog.dismiss();
					cdt.cancel();
				}
			});
			
		}
		
		alertDialog.setCanceledOnTouchOutside(false);
		
		alertDialog.show();
		
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
	    		mp.start();
		    	if(curr_set_no == max_set_no){
		    		end();
		    	}

		    }
		}.start();
		
	}
	
	public void end(){
		Intent returnIntent = new Intent();
		setResult(Activity.RESULT_OK, returnIntent);
		finish();
	}
	
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    if(alertDialog != null && cdt != null){
	    	alertDialog.dismiss();
	    	cdt.cancel();
	    }
	    
	    overridePendingTransition(R.anim.slide_in_left, android.R.anim.fade_out);   
	}
}
