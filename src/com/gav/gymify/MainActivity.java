package com.gav.gymify;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.gav.sqlhelper.DatabaseHelper;
import com.gav.sqlmodel.Day;
import com.gav.sqlmodel.Day.WeekDay;

public class MainActivity extends ActionBarActivity {
	
	private DatabaseHelper db;
	private ListView listView;
	private DayListViewAdapter adapter;
	
	//initialise database and the list view - retrieve the list items from the database
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		overridePendingTransition(R.anim.slide_in_left, android.R.anim.fade_out);
		db = new DatabaseHelper(getApplicationContext());

		ArrayList<Day> days = (ArrayList<Day>) db.getAllDays();
		
		adapter = new DayListViewAdapter(this, R.layout.day_list_item_layout, days);
		
		initListView(adapter);
		
		db.closeDB();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
		case R.id.action_add_day:
			displayAddDayDialog();
			return true;
		case R.id.action_delete:
			return true;		
		case R.id.action_report:
			Intent intent = new Intent(MainActivity.this, ReportActivity.class);
			startActivity(intent);
			return true;
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
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	public void displayAddDayDialog(){
		//layout inflater and View for the Add day Dialog
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.add_day_dialog, (ViewGroup) findViewById(R.id.layout_root));
		
		final EditText nameBox = (EditText) layout.findViewById(R.id.day_name_edit);
		final Spinner weekdaySpinner = (Spinner) layout.findViewById(R.id.weekday_spinner);
		//spinner adapter for the days of the week
		ArrayAdapter<String>spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Day.weekdays);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		weekdaySpinner.setAdapter(spinnerAdapter);
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		builder.setTitle("Add Workout Day");
		
		builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//ensure at least a name is given in order to create a day
				if(!nameBox.getText().toString().isEmpty()){
					Day d = new Day(nameBox.getText().toString(), WeekDay.values()[weekdaySpinner.getSelectedItemPosition()]);
					int id = (int)db.createDay(d, new long[]{});
					d.setId(id);
					adapter.clear(); adapter.addAll(db.getAllDays());
					adapter.notifyDataSetChanged();
				}
				else{
					Toast.makeText(getApplicationContext(), "Please enter a name in the name field", Toast.LENGTH_LONG).show();
				}

			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		AlertDialog dialog = builder.create();
		//prevent touching outside from dismissing the box - forces the use of the cancel button
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	
	public void initListView(final DayListViewAdapter adapter){
		//initialise the list view and its adapter. Add functions to deal with multiple selects and deletes
		listView = (ListView) findViewById(R.id.main_list);
		listView.setAdapter(adapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(new MultiSelectListener(adapter, listView){
			
			//all the default functionality of MultiChoiceModeListener is implemented in MultiSelectListener - only the onActionItemClicked must be implemented
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				
				switch (item.getItemId()) {
                case R.id.action_delete:
                    // Calls getSelectedIds method from ListViewAdapter Class
                    SparseBooleanArray selected = adapter.getSelectedIds();
                    // Captures all selected ids with a loop
                    for (int i = (selected.size() - 1); i >= 0; i--) {
                        if (selected.valueAt(i)) {
                            Day selectedDay = (Day) adapter.getItem(selected.keyAt(i));
                            // Remove selected days from the database using its id
                            db.deleteDay(selectedDay.getId());
                            adapter.remove(selectedDay);
                        }
                    }
                    // Close CAB
                    mode.finish();
                    return true;
                default:
                    return false;
                }
			}
			
		});
		//open the exercise list for the day on click
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				Day d = (Day)listView.getItemAtPosition(position);
				Intent exerciseIntent = new Intent(MainActivity.this, ExerciseActivity.class);
				exerciseIntent.putExtra("day_id", d.getId());
				startActivity(exerciseIntent);
				overridePendingTransition(R.anim.slide_out_right, android.R.anim.fade_out);
			}
			
		});
	}
	
}
