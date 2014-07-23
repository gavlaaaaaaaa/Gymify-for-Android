package com.gav.gymify;

import java.util.List;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public abstract class ListViewAdapter<T> extends ArrayAdapter<T>{
	
	
	protected List<T> list;
	protected Context context;
	protected LayoutInflater inflater;
    private SparseBooleanArray mSelectedItemsIds;
    
    
    public ListViewAdapter(Context context, int resourceId, List<T> list) {
        super(context, resourceId, list);
        mSelectedItemsIds = new SparseBooleanArray();
        this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(context);
    }
    
    public abstract View getView(int position, View view, ViewGroup parent);
    
    public void remove(T object) {
        list.remove(object);
        notifyDataSetChanged();
    }
 
    public List<T> getDays() {
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
