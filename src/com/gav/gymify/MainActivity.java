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
import android.widget.AbsListView.MultiChoiceModeListener;
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
	private ListViewAdapter adapter;
	public static final String[] weekdays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		db = new DatabaseHelper(getApplicationContext());

		ArrayList<Day> days = (ArrayList<Day>) db.getAllDays();
		
		adapter = new ListViewAdapter(this, R.layout.day_list_item_layout, days);
		
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
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.add_day_dialog, (ViewGroup) findViewById(R.id.layout_root));
		
		final EditText nameBox = (EditText) layout.findViewById(R.id.day_name_edit);
		final Spinner weekdaySpinner = (Spinner) layout.findViewById(R.id.weekday_spinner);
		ArrayAdapter<String>spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, weekdays);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		weekdaySpinner.setAdapter(spinnerAdapter);
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		builder.setTitle("Add Workout Day");
		
		builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
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
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	
	public void initListView(final ListViewAdapter adapter){
		listView = (ListView) findViewById(R.id.main_list);
		listView.setAdapter(adapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			
			@Override
			public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onDestroyActionMode(ActionMode mode) {
				adapter.removeSelection();
			}
			
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				mode.getMenuInflater().inflate(R.menu.delete_menu, menu);
				return true;
			}
			
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				
				switch (item.getItemId()) {
                case R.id.action_delete:
                    // Calls getSelectedIds method from ListViewAdapter Class
                    SparseBooleanArray selected = adapter
                            .getSelectedIds();
                    // Captures all selected ids with a loop
                    for (int i = (selected.size() - 1); i >= 0; i--) {
                        if (selected.valueAt(i)) {
                            Day selectedDay = (Day) adapter
                                    .getItem(selected.keyAt(i));
                            // Remove selected items following the ids
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
			
			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position,
					long id, boolean checked) {
			 
				final int checkedCount = listView.getCheckedItemCount();
				mode.setTitle(checkedCount + " Items Selected");
				adapter.toggleSelection(position);
			}
		});
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
