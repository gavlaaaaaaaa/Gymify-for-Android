package com.gav.gymify;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gav.sqlhelper.DatabaseHelper;
import com.gav.sqlmodel.Exercise;
import com.gav.sqlmodel.Set;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle.GridStyle;

public class ReportDetailsFragment extends Fragment {
	
	ArrayList<Exercise> exercises;
	GraphView graph;
	DatabaseHelper db;
	ArrayList<String> legendText;
	
	public ReportDetailsFragment(DatabaseHelper db, int mgroup){
		super();
		exercises = (ArrayList<Exercise>)db.getExercisesByMuscleGroup(mgroup);
		this.db = db;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		graph = initialiseGraph(exercises, container.getContext().getApplicationContext());

        View rootView = inflater.inflate(R.layout.fragment_report_details, container, false);
        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.graphLayout);
		layout.addView(graph);
		TextView tv = (TextView) rootView.findViewById(R.id.legend);

		tv.setText("");
		for(String s : legendText){
			tv.setText(tv.getText() + s + "\n");
		}
		tv.setAlpha(0.6F);
        return rootView;
    }
	
	public GraphView initialiseGraph(ArrayList<Exercise> exs, Context context){
		
		final GraphView graphView = new BarGraphView(context, "Max weight lifted last set per exercise");
		graphView.getGraphViewStyle().setGridColor(Color.BLACK);
		
		graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.WHITE);
		graphView.getGraphViewStyle().setVerticalLabelsColor(Color.WHITE);
		graphView.getGraphViewStyle().setGridStyle(GridStyle.HORIZONTAL);
		graphView.setManualYMinBound(0);
		graphView.setViewPort(0, exercises.size());

		legendText = new ArrayList<String>();
		ArrayList< ArrayList<Set> > sets = new ArrayList< ArrayList<Set> >();
		String[] labels = new String[exercises.size()];
		int j = 0;
		for (Exercise e : exercises){
			String[] s = e.getName().split("\\s+");
			String temp = "";
			for(int i = 0; i < s.length; i++)
			{
				temp += s[i].charAt(0);
			}
			labels[j] = temp;
			legendText.add(temp + ": " + e.getName());
			sets.add((ArrayList<Set>)db.getSetsByExercise(e.getId()));
			j++;
		}
		graphView.setHorizontalLabels(labels);

		GraphViewData[] data = new GraphViewData[sets.size()];
		for (int i = 0; i < sets.size(); i++){
			
			ArrayList<Set> currSet = sets.get(i);

			if(!currSet.isEmpty()){
				data[i] = new GraphViewData(i, currSet.get(currSet.size()-1).getWeight());
			}
			else{
				data[i] = new GraphViewData(i, 0);
			}
		}
		GraphViewSeries dataSeries = new GraphViewSeries(data);
		graphView.addSeries(dataSeries);

		return graphView;
	}
}
