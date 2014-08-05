package com.gav.gymify;

import com.gav.gymify.adapter.ListViewAdapter;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;

// class providing default implementation of MultiChoiceModeListener but enforces implementation of onActionItemClicked function
public abstract class MultiSelectListener implements MultiChoiceModeListener {

	private ListViewAdapter<?> adapter;
	private ListView listView;
	
	public MultiSelectListener(ListViewAdapter<?> adapter, ListView listView){
		this.adapter = adapter;
		this.listView = listView;
	}
	
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
	public abstract boolean onActionItemClicked(ActionMode mode, MenuItem item);
	
	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position,
			long id, boolean checked) {
	 
		final int checkedCount = listView.getCheckedItemCount();
		mode.setTitle(checkedCount + " Items Selected");
		adapter.toggleSelection(position);
	}

}
