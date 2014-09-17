package com.joncairo.android.todo;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class ArchivedTodoFragment extends ListFragment {
	private ArrayList<Todo> mArchivedTodos;
	private static final String TAG = "ArchivedCrimeListFragment";
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Todo t = (Todo)(getListAdapter()).getItem(position);
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getactivity returns the hosting activity
        // then you set the title of the activity depending on what
        // the fragment is doing.
        // get the list of crimes
        mArchivedTodos = ToDoListArchived.get(getActivity()).getTodosArchived();
        
        // make an adapter for the listview
        ArrayAdapter<Todo> adapter = new ArrayAdapter<Todo>(getActivity(),
        		android.R.layout.simple_list_item_1,
        		mArchivedTodos);
        // use the built in method from listFragment 
        // to set the adapter for the list.
        setListAdapter(adapter);
	}
	
	// adapted from http://stackoverflow.com/questions/12485698/using-contextual-action-bar-with-fragments
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// set up the contextual action bar so multiple items in the
		// list of todos can be selected and acted on at the same time.
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		//getListView().setFocusable(false);
	    getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

	    	@Override
	        public boolean onItemLongClick(AdapterView<?> parent, View view,
	                    int position, long id) {
	                ((ListView) parent).setItemChecked(position,
	                        ((ListView) parent).isItemChecked(position));
	                return false;
	            }
	        });
	               
	        getListView().setMultiChoiceModeListener(new MultiChoiceModeListener() {

	        private int nr = 0;

	        @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        	getActivity().getMenuInflater().inflate(R.menu.action_bar_menu,
	                        menu);
	            return true;
	            }

	        @Override
	        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	        	return false;
	        }

	        @Override
	        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	        	switch (item.getItemId()) {
	                // heres where we handle all the button clicks in the action bar.
	                case R.id.item1:
	                    Toast.makeText(getActivity(), "Option1 clicked",
	                            Toast.LENGTH_SHORT).show();
	                    break;
	                case R.id.item2:
	                    Toast.makeText(getActivity(), "Option2 clicked",
	                            Toast.LENGTH_SHORT).show();
	                    break;

	                }
	                return false;
	            }

	        @Override
	        public void onDestroyActionMode(ActionMode mode) {
	        	nr = 0;
	        }

	        @Override
	        public void onItemCheckedStateChanged(ActionMode mode,
	        	int position, long id, boolean checked) {
	                if (checked) {
	                    nr++;
	                } else {
	                    nr--;
	                }
	                mode.setTitle(nr + " rows selected!");
	            }
	        });
		}
	
	static ArchivedTodoFragment newInstance(int num) {
		ArchivedTodoFragment f = new ArchivedTodoFragment();
		return f;
	}

}