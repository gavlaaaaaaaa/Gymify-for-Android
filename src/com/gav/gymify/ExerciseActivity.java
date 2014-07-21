package com.gav.gymify;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.gav.sqlhelper.DatabaseHelper;
import com.gav.sqlmodel.Day;
import com.gav.sqlmodel.Exercise;

public class ExerciseActivity extends Activity {

	private DatabaseHelper db;
	private ListView listView;
	private ExerciseListViewAdapter adapter;
	private int day_id;
	private Exercise next_exercise = null;
	private int next_pos = 0;
	private Exercise selected = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exercise);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		day_id = getIntent().getIntExtra("day_id", 0);
		
		db = new DatabaseHelper(getApplicationContext());
		Day currDay = db.getDay(day_id);
		this.getActionBar().setTitle(currDay.getName());
		
		ArrayList<Exercise> exercises = (ArrayList<Exercise>) db.getExercisesByDay(day_id);
		
		adapter = new ExerciseListViewAdapter(this, R.layout.ex_list_item_layout, exercises);
		
		initListView(adapter);
		
		db.closeDB();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.exercise, menu);
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
		case R.id.action_add_exercise:
			displayAddExerciseDialog();
			return true;
		case R.id.action_delete:
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
			View rootView = inflater.inflate(R.layout.fragment_exercise,
					container, false);
			return rootView;
		}
	}
	
	public void displayAddExerciseDialog(){
		// set up layout
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.add_exercise_dialog, (ViewGroup) findViewById(R.id.layout_root));
		
		//initialise dialog items
		final AutoCompleteTextView nameBox = (AutoCompleteTextView) layout.findViewById(R.id.ex_name_edit);
		final EditText descBox = (EditText) layout.findViewById(R.id.desc_edit);
		final EditText setNoBox = (EditText) layout.findViewById(R.id.set_no_edit);
		final Spinner mgroupSpinner = (Spinner) layout.findViewById(R.id.mgroup_spinner);
		//set up spinner
		ArrayAdapter<String>spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Exercise.mgroups);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mgroupSpinner.setAdapter(spinnerAdapter);
		
		//initialise exercise array for autocomplete
		final ArrayList<Exercise> exercises = (ArrayList<Exercise>)db.getAllExercises();
		ArrayAdapter<Exercise> nameAdapter = new ArrayAdapter<Exercise>(this, android.R.layout.simple_list_item_1, exercises);
		nameBox.setAdapter(nameAdapter);
		
		nameBox.setOnItemClickListener(new OnItemClickListener() {


			@Override
			public void onItemClick(AdapterView<?> pView, View view, int pos,
					long arg3) {
				selected = (Exercise)pView.getAdapter().getItem(pos);
				mgroupSpinner.setSelection(((ArrayAdapter<String>)mgroupSpinner.getAdapter()).getPosition(Exercise.mgroups[selected.getMgroup().getMask()]));
				setNoBox.setText(String.valueOf(selected.getNoSets()));
				if(!selected.getDescription().isEmpty()){
					descBox.setText(selected.getDescription());
				}
			}
			
		});
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		builder.setTitle("Add Exercise");
		
		builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(!nameBox.getText().toString().isEmpty()){
					
					if(selected == null){
						selected = new Exercise(nameBox.getText().toString(), Exercise.MuscleGroup.values()[mgroupSpinner.getSelectedItemPosition()], 
							descBox.getText().toString(), Integer.parseInt(setNoBox.getText().toString()));
						if(setNoBox.getText().toString().isEmpty()){
							selected.setNoSets(3);
						}
						int id = (int)db.createExercise(selected, new long[]{});
						
						selected.setId(id);
					}
					db.createDayExercise(day_id, selected.getId());
					adapter.clear(); adapter.addAll(db.getExercisesByDay(day_id));
					adapter.notifyDataSetChanged();
					selected = null;
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
	
	
	public void initListView(final ExerciseListViewAdapter adapter){
		listView = (ListView) findViewById(R.id.exercise_list);
		listView.setAdapter(adapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			
			@Override
			public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
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
                            Exercise selectedExercise = (Exercise) adapter
                                    .getItem(selected.keyAt(i));
                            // Remove selected items following the ids
                            db.removeExerciseFromDay(day_id, selectedExercise.getId());
                            adapter.remove(selectedExercise);
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
				Exercise e = (Exercise)listView.getItemAtPosition(position);
				
				Intent exerciseIntent = new Intent(ExerciseActivity.this, SetActivity.class);
				exerciseIntent.putExtra("exercise_id", e.getId());
				if(position+1 < adapter.getCount() ){
					next_exercise = (Exercise)listView.getItemAtPosition(position+1);
					next_pos = position +1;
				}
				else{
					next_exercise = null;
				}
				exerciseIntent.putExtra("next_exercise_id", next_exercise.getId());
				startActivityForResult(exerciseIntent, 1);
				overridePendingTransition(R.anim.slide_out_right, android.R.anim.fade_out);
			}
			
		});
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	    if (requestCode == 1) {
	        if(resultCode == RESULT_OK){
	            if(next_exercise != null){
	            	listView.performItemClick(listView.getAdapter().getView(next_pos, null, null), next_pos, listView.getAdapter().getItemId(next_pos));
	            }
	            else{
	            	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        		builder.setTitle("Finish Workout?");
	        		
	        		builder.setPositiveButton("End", new DialogInterface.OnClickListener() {
	        			
	        			@Override
	        			public void onClick(DialogInterface dialog, int which) {
	        				// TODO show workout summary
	        				onBackPressed();
	        				Intent intent = new Intent(ExerciseActivity.this, ReportActivity.class);
	        				startActivity(intent);
	        			}
	        		});
	        		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        			
	        			@Override
	        			public void onClick(DialogInterface dialog, int which) {
	        				dialog.dismiss();
	        			}
	        		});
	        		
	        		AlertDialog dialog = builder.create();
	        		dialog.show();
	            }
	        }
	    }
	}
	
	
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    overridePendingTransition(R.anim.slide_in_left, android.R.anim.fade_out);   
	}
}
