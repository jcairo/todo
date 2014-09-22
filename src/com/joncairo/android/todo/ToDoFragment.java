package com.joncairo.android.todo;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

// import com.joncairo.android.todo.BaseToDoFragment.ToDoAdapter;

public class ToDoFragment extends ListFragment {
	public ArrayList<Todo> mTodos;
	public EditText mNewToDoName;
	public Button mDoIt;
	public ToDoAdapter adapter;
	private ListView mListView;
	private static final String TAG = "ToDoListFragment";
	OnToDoItemArchived mToDoArchivedCallback;
	DataLoader mDataLoader;
	// this is the identifier set in the constructor as to whether the
	// instance is the todolist or the archived todolist
	String mKeyNameForToDoList;
	Integer mMenuId;
	
	
	// This interface should be implemented in parent activity
	// it is used to communicate to the Archived todo fragment
	// so that todos can be archived.
	public interface OnToDoItemArchived{
		public void onToDoArchived(ArrayList<Todo> todos, String listName);
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
        mTodos = mDataLoader.getData(mKeyNameForToDoList);
        //ToDoList mLoadedData = mDataLoader.getData(mDataLoaderListName);
        //mTodos = mLoadedData.getTodos();
        
        
        
        // make an adapter for the listview
        adapter = new ToDoAdapter(mTodos);
        setListAdapter(adapter);
        
        // set the contextual menu bar layout todos and archived todos
        // have slightly different contextual action bar menus
        if (mKeyNameForToDoList == "TODOLIST"){
        	mMenuId = R.menu.action_bar_menu;
        } else if (mKeyNameForToDoList == "ARCHIVEDTODOLIST"){
        	mMenuId = R.menu.archived_action_bar_menu;
        }
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
	                getActivity().getMenuInflater().inflate(mMenuId,
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
	                // heres where we handle all the button clicks in the contextual action bar.
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
	                
	                case R.id.unarchive:
                		archiveSelectedItems();
                		mode.finish();
                		break;
                		
	                case R.id.archived_delete:
                		// call subroutine to delete the items currently selected				
						deleteSelectedItems();				
						mode.finish();
						break;
	                case R.id.archived_email:
                    	emailSelectedItems();
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
	
	public void onListItemClick(ListView listView, View v, int position, long id) {
        //Todo t = (Todo)(getListAdapter()).getItem(position);
		Todo t = ((ToDoAdapter)getListAdapter()).getItem(position);
	   	if (t.getDone()){
	   		t.setDone(false);
	   	} else {
	   		t.setDone(true);
    	}
    	Log.v("TAG", "List item clicked");
    }		
	
		
		
	// heres where we need to save the state of the list
	@Override 
	public void onPause(){			
		super.onPause();
		Log.v("DATASAVETEST", mTodos.toString());
		mDataLoader.setData(this.mKeyNameForToDoList, mTodos);
		
	}
		
	static ToDoFragment newInstance(int num, String storageKeyNameForToDoList) {
		ToDoFragment f = new ToDoFragment();
		// set the key name so the todolist references the todos
		// and the archived list refernces the archived list.
		f.mKeyNameForToDoList = storageKeyNameForToDoList;
		// this variable identifies the list as either the todo list or the archived todo list
		return f;
	}
	
	public void archiveSelectedItems(){
		mListView = getListView();
        SparseBooleanArray chosenItemsPositions = mListView.getCheckedItemPositions();
        // now that we have the positions iterate through them and remove them
        // from the todolist then update the adapter
		// build arraylist of todos to pass to the archive
        ArrayList<Todo> mToDosToBeArchived = new ArrayList<Todo>();
        for (int index = 0; index<chosenItemsPositions.size(); index++){
        	if(chosenItemsPositions.valueAt(index)){
        		// this funky bit adjust for the fact that when we delete
        		// something from the arraylist of todoitems the positions
        		// in the list change. To adjust you just subtract the number
        		// of items already removed from the position your deleting.
        		Todo toDoToBeArchived = mTodos.get(chosenItemsPositions.keyAt(index)-index);
        		mToDosToBeArchived.add(toDoToBeArchived);            
        		mTodos.remove(chosenItemsPositions.keyAt(index)-index);
        		adapter.notifyDataSetChanged();                 	
        	}
        }	

        mToDoArchivedCallback.onToDoArchived(mToDosToBeArchived, this.mKeyNameForToDoList);
	}
	
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
        		//removeItemFromToDoList(chosenItemsPositions.keyAt(index)-index);
        		mTodos.remove(chosenItemsPositions.keyAt(index)-index);
        		adapter.notifyDataSetChanged(); 
        	}
        }
	}
	
	public void emailSelectedItems(){
		mListView = getListView();
		SparseBooleanArray chosenItemsPositions = mListView.getCheckedItemPositions();
		Emailer emailer = new Emailer(getActivity());
		emailer.emailSparseBooleanArray(chosenItemsPositions, mTodos);
	}

	// create the custom adapter which manages the list of todo's
	// adapted from The Big Nerd Ranch Guide p 189
	class ToDoAdapter extends ArrayAdapter<Todo> {
		public ToDoAdapter(ArrayList<Todo> todo) {
			super(getActivity(), 0, todo);
		}
		
		@Override
	    public View getView(final int position, View convertView, ViewGroup parent) {
	        // If we weren't given a view, inflate one
	        if (convertView == null) {
	            convertView = getActivity().getLayoutInflater()
	                .inflate(R.layout.list_view_layout, null);
	        }
	        // Configure the view for the todo
	        Todo t = getItem(position);
	        TextView titleTextView =
	        		(TextView)convertView.findViewById(R.id.todo_list_item_titleTextView);
	        titleTextView.setText(t.getToDoName());
	        TextView dateTextView =
	        		(TextView)convertView.findViewById(R.id.todo_list_item_dateTextView);
	        dateTextView.setText(t.getDateCreated().toString());
	        CheckBox solvedCheckBox =
	        		(CheckBox)convertView.findViewById(R.id.todo_list_item_doneCheckBox);
	        solvedCheckBox.setChecked(t.getDone());

	        
	        // set up the checkbox check listner
	        View row_layout_view = convertView;
	        final CheckBox checkBox = (CheckBox) row_layout_view.findViewById(R.id.todo_list_item_doneCheckBox);
	        // checkbox.setOnClickListener adapted from 
	        // http://stackoverflow.com/questions/15941635/how-to-add-a-listener-for-checkboxes-in-an-adapter-view-android-arrayadapter
	        checkBox.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final boolean isChecked = checkBox.isChecked();
					Log.v(TAG, "checbox clicked inside adapter");
					// update the data model
					mTodos.get(position).setDone(isChecked);
					adapter.notifyDataSetChanged();				
				}
			});
	        
	        return convertView;
		}
	}
	
	public void addItemToToDoList(ArrayList<Todo> toDosToBeAdded, String fromListName){
		// the from listName variable represents which list the todos
		// are coming from, so if they come from archivedlist we setArchive property
		// to be false on each before they are entered and vis a vis when from the 
		// todolist. When something is added from the task input field we just pass
		// in an empty string so the archiving doesn't get set as its set to unarchived
		// by default on entry.
		for (int i = 0; i < toDosToBeAdded.size(); i++){
			Todo todo = toDosToBeAdded.get(i);
			// if the todo is coming from the todolist we
			// we need to set it as an archived to do.
			// otherwise its come from the archive and
			// needs to be unarchived.
			if (fromListName == "TODOLIST"){
				todo.setArchived(true);
			} else if (fromListName == "ARCHIVEDTODOLIST") { 
				todo.setArchived(false);
			} else {
				// this is a little akward I know
				// if the to do is coming from the input text
				// box setArchive does not need to be set
				// so we pass in an empty string to dodge the above
				// conditions
			}
			mTodos.add(0, todo);
			adapter.notifyDataSetChanged();
		}
	}
	
	public ArrayList<Integer> totalCheckedAndUncheckedItems(){
		ArrayList<Integer> checkedUnchedCountArray = new ArrayList<Integer>();
		Integer totalUnchecked = 0;
		Integer totalChecked = 0;
		for (int i = 0; i<mTodos.size(); i++){
			if(mTodos.get(i).getDone()){
				totalChecked++;
			} else {
				totalUnchecked++;
			}
		}
		checkedUnchedCountArray.add(totalChecked);
		checkedUnchedCountArray.add(totalUnchecked);
		return checkedUnchedCountArray;
	}
}
