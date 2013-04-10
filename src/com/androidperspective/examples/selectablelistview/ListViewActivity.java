package com.androidperspective.examples.selectablelistview;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu; 
import com.actionbarsherlock.view.MenuItem;

import android.content.Context;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListViewActivity extends  SherlockListActivity {

	private ActionMode mActionMode;
	private String[] mItems = {"Albert Einstein", "Isaac Newton", "Galileo Galilei", "Thomas Edison"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_view);
		
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
			// no items selected, so we can perform item click actions
		} else
			onListItemCheck(position);		
	}
	
	private void onListItemCheck(int position) {
        SelectableAdapter adapter = (SelectableAdapter) getListAdapter();
        adapter.toggleSelection(position);
        boolean hasCheckedItems = adapter.getSelectedCount() > 0;

        if (hasCheckedItems && mActionMode == null)
            mActionMode = startActionMode(new MyActionModeCallback());
        else if (!hasCheckedItems && mActionMode != null)
            mActionMode.finish();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}
	
	private class SelectableAdapter extends ArrayAdapter<String>{

		private SparseBooleanArray mSelectedItemsIds;
        private int mSelectedCount;
		
		public SelectableAdapter(Context context, String[] objects) {
			super(context, R.layout.selectable_list_item, R.id.item_text, objects);
			mSelectedItemsIds = new SparseBooleanArray();
            mSelectedCount = 0;
		}
		
		public void toggleSelection(int position)
        {
            selectView(position, !mSelectedItemsIds.get(position));
        }

        public void removeSelection() {
            mSelectedItemsIds = new SparseBooleanArray();
            mSelectedCount = 0;
            notifyDataSetChanged();
        }

        public void selectView(int position, boolean value)
        {
            boolean oldValue = mSelectedItemsIds.get(position);

            if(value)
                mSelectedItemsIds.put(position, value);
            else
                mSelectedItemsIds.delete(position);

            if (oldValue != value) {
                if (value) 
                    mSelectedCount++;
                else
                    mSelectedCount--;                
            }
            notifyDataSetChanged();
        }
        
        public int getSelectedCount() {
            return mSelectedCount;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	
        	ViewHolder viewHolder;       	
        	
        	if(convertView == null){
        		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        		convertView = inflater.inflate(R.layout.selectable_list_item, null);
        		
        		viewHolder = new ViewHolder();
        		viewHolder.iconSelected = (ImageView) convertView.findViewById(R.id.item_icon_selected);
        		viewHolder.text = (TextView) convertView.findViewById(R.id.item_text);
        		
        		convertView.setTag(viewHolder);
        	} else
        		viewHolder = (ViewHolder) convertView.getTag();
        	
        	viewHolder.text.setText(getItem(position));
        	
        	int itemVisibility = mSelectedItemsIds.get(position)? View.VISIBLE: View.GONE;
        	viewHolder.iconSelected.setVisibility(itemVisibility);
        	
        	return super.getView(position, convertView, parent);
        }
        
        private class ViewHolder {
        	public ImageView iconSelected;
        	public TextView text;
        }
	}
	
	private class MyActionModeCallback implements ActionMode.Callback {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// inflating the contextual menu
			mode.getMenuInflater().inflate(R.menu.contextual_list_view, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			// here you can delete selected items
			mode.finish();
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			SelectableAdapter adapter = (SelectableAdapter) getListAdapter();
			adapter.removeSelection();
			mActionMode = null;
		}
		
	}

}
