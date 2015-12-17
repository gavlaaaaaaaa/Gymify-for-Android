package com.gav.gymify.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gav.gymify.R;
import com.gav.gymify.SetActivity;
import com.gav.gymify.R.id;
import com.gav.gymify.R.layout;
import com.gav.sqlhelper.DatabaseHelper;
import com.gav.sqlmodel.Set;

public class CardAdapter extends ListViewAdapter<Set>{
	
	private List<Set> previous;
	private DatabaseHelper db;
	private int ex_id;
	private int max_set_no;
	private int curr_set_no;
	private ArrayList<Boolean> doneSets;
	int currPos = 0;
	
	
	public CardAdapter(Context context, int resource, List<Set> list, List<Set> previous, int ex_id) {
		super(context, resource, list);
		inflater = LayoutInflater.from(context);
		this.previous = previous;
		db = new DatabaseHelper(this.getContext().getApplicationContext());
		this.ex_id = ex_id;
		curr_set_no = 0;
		doneSets = new ArrayList<Boolean>();
		
	}
	
	private class ViewHolder{
		TextView setNumber;
		EditText weight;
		EditText reps;
		ImageButton confirm;
		
	}
	
	public View getView(final int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.card_row, parent, false);
           
            // Locate the TextViews in listview_item.xml
            holder.setNumber = (TextView) view.findViewById(R.id.set_number);
            holder.weight = (EditText) view.findViewById(R.id.weight);
            holder.reps = (EditText) view.findViewById(R.id.reps);
            holder.confirm = (ImageButton) view.findViewById(R.id.confirm_btn);
    		
    		holder.confirm.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(!holder.weight.getText().toString().isEmpty() ){//&& !holder.reps.getText().toString().isEmpty()){
						Time now = new Time();
						now.setToNow();
						Set s = new Set(now.format("%H:%M - %m-%d-%Y"), 0, Double.valueOf(holder.weight.getText().toString()));
						int id = (int)db.createSet(s);
						s.setId(id);
						db.createExerciseSet(ex_id, id);
						Toast.makeText(getContext(), "Set Saved", Toast.LENGTH_LONG).show();
						holder.weight.setFocusable(false);
						holder.reps.setFocusable(false);
						holder.confirm.setClickable(false);
						((View)v.getParent()).setBackgroundColor(Color.parseColor("#194775"));
						doneSets.add(true);
						currPos++;
						curr_set_no++;
						startTimer();
						addSet(s);
					}
					else{
						Toast.makeText(getContext(), "Fill in the weight and rep values", Toast.LENGTH_LONG).show();
					}
				}
			});

    		if(previous.size() > position && (max_set_no - position) > 0){
    			if(previous.size() > max_set_no){
    				holder.weight.setText(String.valueOf(previous.get(previous.size()-(max_set_no-position)).getWeight()));
    				holder.reps.setText(String.valueOf(previous.get(previous.size()-(max_set_no-position)).getNoReps()));
    			}
    			else{
    				holder.weight.setText(String.valueOf(previous.get(position).getWeight()));
    				holder.reps.setText(String.valueOf(previous.get(position).getNoReps()));
    			}
            }
            else{
            	holder.weight.setHint("0");
            	holder.reps.setHint("0");
            }
    		 
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Capture position and set to the TextViews
        holder.setNumber.setText("Set " + (position+1));
        
        return view;
    }
	
	public void startTimer(){
		((SetActivity)context).startTimer(curr_set_no, max_set_no);
	}

	public void addSet(Set set){
		if(this.curr_set_no <= getViewTypeCount()){
			list.add(set);
			notifyDataSetChanged();
		}
		else{
			Toast.makeText(context, "Cannot make any more sets - please start again!", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 15;
    }
	
	public void setMaxSetNo(int setNo){
		this.max_set_no = setNo;
	}
	
	public int getMaxSetNo(){
		return this.max_set_no;
	}
	

}
