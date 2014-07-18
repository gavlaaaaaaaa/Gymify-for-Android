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

public class ListViewAdapter extends ArrayAdapter<Day>{
	
	
	private List<Day> list;
	private Context context;
	private LayoutInflater inflater;
    private SparseBooleanArray mSelectedItemsIds;
    
    
    public ListViewAdapter(Context context, int resourceId, List<Day> list) {
        super(context, resourceId, list);
        mSelectedItemsIds = new SparseBooleanArray();
        this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(context);
    }
    

	private class ViewHolder {
    	
        TextView day;
        TextView weekday;
    }
 
    public View getView(int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.day_list_item_layout, null);
            // Locate the TextViews in listview_item.xml
            holder.day = (TextView) view.findViewById(R.id.dayName);
            holder.weekday = (TextView) view.findViewById(R.id.weekday);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Capture position and set to the TextViews
        holder.day.setText(list.get(position).getName());
        holder.weekday.setText((MainActivity.weekdays[list.get(position).getWeekday().getMask()]));
        
        return view;
    }
    
    public void remove(Day object) {
        list.remove(object);
        notifyDataSetChanged();
    }
 
    public List<Day> getDays() {
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
