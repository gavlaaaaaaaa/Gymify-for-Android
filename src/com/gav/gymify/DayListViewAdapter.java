package com.gav.gymify;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gav.sqlmodel.Day;

public class DayListViewAdapter extends ListViewAdapter<Day>{

	public DayListViewAdapter(Context context, int resourceId, List<Day> list) {
        super(context, resourceId, list);
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
        holder.weekday.setText((Day.weekdays[list.get(position).getWeekday().getMask()]));
        
        return view;
    }
}
