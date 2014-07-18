package com.gav.gymify;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gav.sqlhelper.DatabaseHelper;
import com.gav.sqlmodel.Exercise;
import com.gav.sqlmodel.Set;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.GraphViewStyle.GridStyle;
import com.jjoe64.graphview.LineGraphView;

public class ReportActivity extends Activity {

	private DatabaseHelper db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		this.getActionBar().setTitle("Workout Report");
		db = new DatabaseHelper(getApplicationContext());
		
		initialiseGraphData();
		
		db.closeDB();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.report, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
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
			View rootView = inflater.inflate(R.layout.fragment_report,
					container, false);
			return rootView;
		}
	}
	
	public void initialiseGraphData(){
		Random rand = new Random();
		final GraphView graphView = new LineGraphView(this, "Weight for last 5 sets per exercise");
		graphView.getGraphViewStyle().setGridColor(Color.GRAY);
		graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.WHITE);
		graphView.getGraphViewStyle().setVerticalLabelsColor(Color.WHITE);
		graphView.getGraphViewStyle().setGridStyle(GridStyle.HORIZONTAL);
		graphView.setShowLegend(true);
		graphView.setLegendWidth(500);
		graphView.setLegendAlign(LegendAlign.BOTTOM);
		graphView.setViewPort(0, 5);
		graphView.setScalable(true);
		
		
		graphView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				graphView.setShowLegend(!graphView.isShowLegend());
				graphView.redrawAll();
				
			}
		});
		LinearLayout layout = (LinearLayout) findViewById(R.id.graphLayout);
		layout.addView(graphView);
		
		ArrayList<Exercise> exercises = (ArrayList<Exercise>) db.getAllExercises();
		ArrayList< ArrayList<Set> > sets = new ArrayList< ArrayList<Set> >();
		for (Exercise e : exercises){
			sets.add((ArrayList<Set>)db.getSetsByExercise(e.getId()));
		}
		
		GraphViewData[] data;

		for (int i = 0; i < sets.size(); i++){
			
			ArrayList<Set> currSet = sets.get(i);

			int j;
			if(currSet.size() >= 5){
				j = currSet.size()-5;
				data = new GraphViewData[5];
			}
			else{
				j = 0;
				data = new GraphViewData[currSet.size()];
			}
			int x = 0;
			for(j=j; j < currSet.size(); j++){
				data[x] = new GraphViewData(x, currSet.get(j).getWeight());
				x++;
			}
			
			int r = rand.nextInt();
			int g = rand.nextInt();
			int b = rand.nextInt();
						
			GraphViewSeries dataSeries = new GraphViewSeries(exercises.get(i).getName(), new GraphViewSeriesStyle(Color.rgb(r, g, b), 3), data);
			graphView.addSeries(dataSeries);
			
		}
		
	}

}
