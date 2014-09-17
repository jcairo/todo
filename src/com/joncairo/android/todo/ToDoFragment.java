package com.joncairo.android.todo;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
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
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ToDoFragment extends ListFragment {
	private ArrayList<Todo> mTodos;
	private EditText mNewToDoName;
	private Button mDoIt;
	//private ArrayAdapter<Todo> adapter;
	public ToDoAdapter adapter;
	private static final String TAG = "ToDoListFragment";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the host activity
        
        // getactivity returns the hosting activity
        // then you set the title of the activity depending on what
        // the fragment is doing.
        // get the list of crimes
        mTodos = ToDoList.get(getActivity()).getTodos();
        
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
                    Toast.makeText(getActivity(), "Option1 clicked",
                            Toast.LENGTH_SHORT).show();
                    
                    break;
                case R.id.delete:
                    Toast.makeText(getActivity(), "Option2 clicked",
                            Toast.LENGTH_SHORT).show();
                    //ListView mListView = getListView();
                    //long[] longConverts = mListView.getCheckedItemIds();
                    //private List<long> itemIds = new ArrayList<long>(Arrays.asList(longConverts));
                    break;
                
                case R.id.archive:
                	Toast.makeText(getActivity(), "Option3 clicked",
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
				ToDoList mToDoList = ToDoList.get(getActivity().getApplicationContext());
				//adapter.setNotifyOnChange(true);
				mTodos.add(0, newTodo);	
				adapter.notifyDataSetChanged();
			}
		});                
	}
	
    public void onListItemClick(ListView listView, View v, int position, long id) {
        //Todo t = (Todo)(getListAdapter()).getItem(position);
    	Todo t = ((ToDoAdapter)getListAdapter()).getItem(position);
    	t.setDone(true);
    	Log.v("TAG", "List item clicked");
    }
	
	static ToDoFragment newInstance(int num) {
		ToDoFragment f = new ToDoFragment();
		return f;
	}
	
	// create the custom adapter which manages the list of todo's
	// adapted from The Big Nerd Ranch Guide p 189
	private class ToDoAdapter extends ArrayAdapter<Todo> {
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
}

