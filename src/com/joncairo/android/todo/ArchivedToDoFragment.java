package com.joncairo.android.todo;

import java.util.ArrayList;

import com.joncairo.android.todo.BaseToDoFragment.ToDoAdapter;
import com.joncairo.android.todo.ToDoFragment.OnToDoItemArchived;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class ArchivedToDoFragment extends BaseToDoFragment {
	public ArrayList<Todo> mTodos;
	public EditText mNewToDoName;
	public Button mDoIt;
	public ToDoAdapter adapter;
	private ListView mListView;
	private static final String TAG = "ToDoListFragment";
	OnToDoItemUnArchived mToDoUnArchivedCallback;
	
	// This interface should be implemented in parent activity
	// it is used to communicate to the Archived todo fragment
	// so that todos can be archived.
	public interface OnToDoItemUnArchived{
		public void onToDoUnArchived(Todo todo);
	}
	
	// on attach method taken from
	// http://developer.android.com/training/basics/fragments/communicating.html
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
	
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mToDoUnArchivedCallback = (OnToDoItemUnArchived) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnToDoItemUnarchived");
        }
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the host activity
        
        // create a list of todos
        mTodos = new ToDoList(getActivity()).getTodos();
        
        // make an adapter for the listview
        adapter = new ToDoAdapter(mTodos);
        setListAdapter(adapter);       
	}
	
	// adapted from http://stackoverflow.com/questions/12485698/using-contextual-action-bar-with-fragments
	// set up the list items to activate the contextual action bar when
	// long pressed
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// set up the contextual action bar so multiple items in the
		// list of todos can be selected and acted on at the same time.
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
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
                getActivity().getMenuInflater().inflate(R.menu.archived_action_bar_menu,
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
                case R.id.archived_email:
                    emailSelectedItems();
                    mode.finish();
                    break;
                case R.id.archived_delete:
                	// call subroutine to delete the items currently selected				
					deleteSelectedItems();				
                	mode.finish();
                    break;
                
                case R.id.unarchive:
                	unArchiveSelectedItems();
                	mode.finish();
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
                mode.setTitle(nr + " todos selected!");
            }
        });     
	}
	
	static ArchivedToDoFragment newInstance(int num) {
		ArchivedToDoFragment f = new ArchivedToDoFragment();
		return f;
	}
	
	public void unArchiveSelectedItems(){
		mListView = getListView();
        SparseBooleanArray chosenItemsPositions = mListView.getCheckedItemPositions();
        // now that we have the positions iterate through them and remove them
        // from the todolist then update the adapter
		for (int index = 0; index<chosenItemsPositions.size(); index++){
        	if(chosenItemsPositions.valueAt(index)){
        		Todo toDoToBeUnArchived = mTodos.get(index);
        		mToDoUnArchivedCallback.onToDoUnArchived(toDoToBeUnArchived);
        		// this funky bit adjust for the fact that when we delete
        		// something from the arraylist of todoitems the positions
        		// in the list change. To adjust you just subtract the number
        		// of items already removed from the position your deleting.            
        		mTodos.remove(chosenItemsPositions.keyAt(index));
        		adapter.notifyDataSetChanged();                 	
        	}
        }	
	}
}
