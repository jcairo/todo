package com.joncairo.android.todo;

import java.util.ArrayList;
import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.joncairo.android.todo.ToDoFragment.OnToDoItemArchived;

public class MainActivity extends ActionBarActivity implements
		ActionBar.TabListener, OnToDoItemArchived {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	Button mDoIt;
	EditText mNewToDoName;
	ToDoFragment mtoDoFragment;
	ToDoFragment mArchivedToDoFragment;
	String mAppDataFileName = "TODODATAKEY";

	// this is a communication method built to communicate between
	// the todofragment and the activity itself.
	// it should take the todo received and add it to the 
	// archived todo list
	public void onToDoArchived(ArrayList<Todo> todos, String listName){
		// here we need to check which list is sending the todos
		// to be archived/unarchived and use that info to add them to the appropriate
		// list
		// if we are adding to the archive the message is coming from the 
		// "TODOLIST", if its coming the "ARCHIVEDTODOLIST then we add it
		// to the TODOLIST
		if (listName == "TODOLIST"){
			mArchivedToDoFragment.addItemToToDoList(todos, listName);
		} else if (listName == "ARCHIVEDTODOLIST") {
			mtoDoFragment.addItemToToDoList(todos, listName);
		}
	}
	
	// this is a communication method built to communicate between
	// the todofragment and the activity itself.
	// it should take the todo received and add it to the 
	// archived todo list
	public void onToDoUnArchived(Todo todo){
		Log.v("Main activity", "main activity sees unarchiving");
		mtoDoFragment.mTodos.add(todo);
		mtoDoFragment.adapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar for tabs
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter to manage tab switching
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		// Wire up the to do text entry field
		final EditText mNewToDoName = (EditText)findViewById(R.id.todo_text);					
		// Wire up the to do enter button
		Button mDoIt = (Button)findViewById(R.id.enter_button);
		mDoIt.setOnClickListener( new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
			ArrayList<Todo> mToDoToBeAdded = new ArrayList<Todo>();
			// then add this as a new to do to the to do array				
			String newToDoText = (String)mNewToDoName.getText().toString();
			Log.v("ButtonTextCheck", newToDoText);
			// Create a new todo instance 
				Todo newTodo = new Todo(newToDoText);
				mToDoToBeAdded.add(newTodo);
				// append it to the todolist array
				mtoDoFragment.addItemToToDoList(mToDoToBeAdded, "");	
			}
		}); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		// create the emailer
		Emailer emailer = new Emailer(this);
		switch (item.getItemId()) {
			case R.id.email_all:
				// get a list of all todos and send the email
				ArrayList<Todo> todosArrayAggregator = mtoDoFragment.mTodos;
				ArrayList<Todo> todosArrayArchivedToBeAdded = mArchivedToDoFragment.mTodos;
				// concatenate the arraylists
				todosArrayAggregator.addAll(todosArrayArchivedToBeAdded);
				emailer.emailArrayList(todosArrayAggregator);
				return true;
			case R.id.email_all_todos:
				ArrayList<Todo> todosArray = mtoDoFragment.mTodos;
				emailer.emailArrayList(todosArray);			
				// get a list of all todos and email
				return true;
			case R.id.email_all_archived:
				ArrayList<Todo> todosArrayArchived = mArchivedToDoFragment.mTodos;
				emailer.emailArrayList(todosArrayArchived);	
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Each page is referred to based on an integer index related to the tab
			// selected
			if (position == 0) {
				mtoDoFragment = ToDoFragment.newInstance(position, "TODOLIST");
				// Log.v("tag of fragment", mtoDoFragment.getTag());
				return mtoDoFragment;			
			} else {
				mArchivedToDoFragment = ToDoFragment.newInstance(position, "ARCHIVEDTODOLIST");
				// Log.v("tag of fragment", mArchivedToDoFragment.getTag());
				return mArchivedToDoFragment;	
			}		
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		@Override
		// set the tab display names
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			}
			return null;
		}
	}
}
