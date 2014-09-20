package com.joncairo.android.todo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

public class ToDoFragment extends BaseToDoFragment {
	public ArrayList<Todo> mTodos;
	public EditText mNewToDoName;
	public Button mDoIt;
	public ToDoAdapter adapter;
	private ListView mListView;
	private String mDataLoaderListName = "TO_DO_LIST";
	private static final String TAG = "ToDoListFragment";
	OnToDoItemArchived mToDoArchivedCallback;
	DataLoader mDataLoader;
	
	// This interface should be implemented in parent activity
	// it is used to communicate to the Archived todo fragment
	// so that todos can be archived.
	public interface OnToDoItemArchived{
		public void onToDoArchived(Todo todo);
	}
	
	// on attach method taken from
	// http://developer.android.com/training/basics/fragments/communicating.html
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
	
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mToDoArchivedCallback = (OnToDoItemArchived) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnToDoItemArchived");
        }
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the host activity
        
        // getactivity returns the hosting activity
        // then you set the title of the activity depending on what
        // the fragment is doing.
        // get the list of todos
        //mTodos = ToDoList.get(getActivity()).getTodos();
        //mTodos = new ToDoList(getActivity()).getTodos();
        
        // call the dataloader and receive back a list of mTodos
        // which is an arraylist of todos
        mDataLoader = new DataLoader(getActivity(), "TO_DO_DATA");
        mTodos = mDataLoader.getData("TO_DO_LIST");
        //ToDoList mLoadedData = mDataLoader.getData(mDataLoaderListName);
        //mTodos = mLoadedData.getTodos();
        
        
        
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
	                case R.id.email:
	                    emailSelectedItems();
	                    mode.finish();
	                    break;
	                case R.id.delete:
	                	// call subroutine to delete the items currently selected				
						deleteSelectedItems();				
	                	mode.finish();
	                    break;
	                
	                case R.id.archive:
	                	archiveSelectedItems();
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
	        
			// Wire up the to do text entry field
			mNewToDoName = (EditText)getActivity().findViewById(R.id.todo_text);
			
			// Wire up the to do enter button
			mDoIt = (Button)getActivity().findViewById(R.id.enter_button);
			mDoIt.setOnClickListener( new View.OnClickListener() {	
				@Override
				public void onClick(View v) {
					// then add this as a new to do to the to do array				
					String newToDoText = (String)mNewToDoName.getText().toString();
					Log.v("ButtonTextCheck", newToDoText);
					// Create a new todo instance 
					Todo newTodo = new Todo(newToDoText);
					// append it to the todolist array
					//adapter.setNotifyOnChange(true);
					mTodos.add(0, newTodo);	
					adapter.notifyDataSetChanged();
				}
			});                
		}
	
	// heres where we need to save the state of the list
	@Override 
	public void onPause(){			
		super.onPause();
		mDataLoader.setData("TO_DO_LIST", mTodos);		
	}
		
	static ToDoFragment newInstance(int num) {
		ToDoFragment f = new ToDoFragment();
		return f;
	}
	
	public void archiveSelectedItems(){
		mListView = getListView();
        SparseBooleanArray chosenItemsPositions = mListView.getCheckedItemPositions();
        // now that we have the positions iterate through them and remove them
        // from the todolist then update the adapter
		for (int index = 0; index<chosenItemsPositions.size(); index++){
        	if(chosenItemsPositions.valueAt(index)){
        		Todo toDoToBeArchived = mTodos.get(index);
        		mToDoArchivedCallback.onToDoArchived(toDoToBeArchived);
        		// this funky bit adjust for the fact that when we delete
        		// something from the arraylist of todoitems the positions
        		// in the list change. To adjust you just subtract the number
        		// of items already removed from the position your deleting.            
        		mTodos.remove(chosenItemsPositions.keyAt(index));
        		adapter.notifyDataSetChanged();                 	
        	}
        }	
	}
	
	@Override
	public void deleteSelectedItems(){
    	mListView = getListView();
        SparseBooleanArray chosenItemsPositions = mListView.getCheckedItemPositions();
        // now that we have the positions iterate through them and remove them
        // from the todolist then update the adapter
		for (int index = 0; index<chosenItemsPositions.size(); index++){
        	if(chosenItemsPositions.valueAt(index)){
        		// this funky bit adjust for the fact that when we delete
        		// something from the arraylist of todoitems the positions
        		// in the list change. To adjust you just subtract the number
        		// of items already removed from the position your deleting.            
        		mTodos.remove(chosenItemsPositions.keyAt(index)-index);
        		adapter.notifyDataSetChanged();                 	
        	}
        }
	}
}
