package com.gav.gymify;

import java.util.List;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gav.sqlmodel.Day;
import com.gav.sqlmodel.Exercise;

public class ExerciseListViewAdapter extends ArrayAdapter<Exercise>{

	
	private List<Exercise> list;
	private Context context;
	private LayoutInflater inflater;
    private SparseBooleanArray mSelectedItemsIds;
    
    
	public ExerciseListViewAdapter(Context context, int resourceId, List<Exercise>list) {
        super(context, resourceId, list);
        mSelectedItemsIds = new SparseBooleanArray();
        this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(context);
    }
	
	private class ViewHolder {
    	
        TextView name;
        TextView mgroup;
    }
 
    public View getView(int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.ex_list_item_layout, null);
            // Locate the TextViews in listview_item.xml
            holder.name = (TextView) view.findViewById(R.id.ex_name);
            holder.mgroup = (TextView) view.findViewById(R.id.mgroup);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Capture position and set to the TextViews
        holder.name.setText(((Exercise) list.get(position)).getName());
        holder.mgroup.setText((Exercise.mgroups[((Exercise) list.get(position)).getMgroup().getMask()]));
        
        return view;
    }
    
    public void remove(Exercise object) {
        list.remove(object);
        notifyDataSetChanged();
    }
 
    public List<Exercise> getExercises() {
        return list;
    }
 
    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
        
    }
 
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }
 
    public void selectView(int position, boolean value) {

        if (value){
            mSelectedItemsIds.put(position, value);
        }
        else{
            mSelectedItemsIds.delete(position);
        }
        notifyDataSetChanged();
    }
 
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }
 
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
	
	
	  
}
