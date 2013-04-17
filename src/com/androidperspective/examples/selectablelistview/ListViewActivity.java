package com.androidperspective.examples.selectablelistview;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu; 
import com.actionbarsherlock.view.MenuItem;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ListViewActivity extends SherlockListActivity {

	private ActionMode mActionMode;
	private String[] mItems = {"Eclair", "Froyo", "Gingerbread", "Honeycomb", "Ice Cream Sandwich", "Jelly Bean"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_view);
		
		//add on long click listener to start action mode
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
				onListItemCheck(position);
				return true;
			}
		});
		
		setListAdapter(new SelectableAdapter(this, mItems));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {		
		if(mActionMode == null) {
			// no items selected, so perform item click actions
		} else
			// add or remove selection for current list item
			onListItemCheck(position);		
	}	
	
	private void onListItemCheck(int position) {
        SelectableAdapter adapter = (SelectableAdapter) getListAdapter();
        adapter.toggleSelection(position);
        boolean hasCheckedItems = adapter.getSelectedCount() > 0;        

        if (hasCheckedItems && mActionMode == null)
        	// there are some selected items, start the actionMode
            mActionMode = startActionMode(new ActionModeCallback());
        else if (!hasCheckedItems && mActionMode != null)
        	// there no selected items, finish the actionMode
            mActionMode.finish();
        

        if(mActionMode != null)
        	mActionMode.setTitle(String.valueOf(adapter.getSelectedCount()) + " selected");
    }
	
	private class SelectableAdapter extends ArrayAdapter<String>{

		private SparseBooleanArray mSelectedItemsIds;
		
		public SelectableAdapter(Context context, String[] objects) {
			super(context, android.R.layout.simple_list_item_1, objects);
			mSelectedItemsIds = new SparseBooleanArray();
		}
		
		public void toggleSelection(int position)
        {
            selectView(position, !mSelectedItemsIds.get(position));
        }

        public void removeSelection() {
            mSelectedItemsIds = new SparseBooleanArray();
            notifyDataSetChanged();
        }

        public void selectView(int position, boolean value)
        {
            if(value)
                mSelectedItemsIds.put(position, value);
            else
                mSelectedItemsIds.delete(position);
            
            notifyDataSetChanged();
        }
        
        public int getSelectedCount() {
            return mSelectedItemsIds.size();// mSelectedCount;
        }
        
        public SparseBooleanArray getSelectedIds() {
        	return mSelectedItemsIds;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	
        	if(convertView == null){
        		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        		convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
        	}         	
        	((TextView) convertView).setText(getItem(position));    
        	//change background color if list item is selected
        	convertView.setBackgroundColor(mSelectedItemsIds.get(position)? 0x9934B5E4: Color.TRANSPARENT);        	
        	
        	return convertView;
        }

	}
	
	private class ActionModeCallback implements ActionMode.Callback {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// inflate contextual menu	
			mode.getMenuInflater().inflate(R.menu.contextual_list_view, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {			
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			// retrieve selected items and print them out
			SelectableAdapter adapter = (SelectableAdapter) ListViewActivity.this.getListAdapter();
			SparseBooleanArray selected = adapter.getSelectedIds();
			StringBuilder message = new StringBuilder();			
			for (int i = 0; i < selected.size(); i++){				
			    if (selected.valueAt(i)) {
			    	String selectedItem = adapter.getItem(selected.keyAt(i));
			    	message.append(selectedItem + "\n");
			    }
			}			
			Toast.makeText(ListViewActivity.this, message.toString(), Toast.LENGTH_LONG).show();
			
			// close action mode
			mode.finish();
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			// remove selection 
			SelectableAdapter adapter = (SelectableAdapter) getListAdapter();
			adapter.removeSelection();
			mActionMode = null;
		}
		
	}

}
