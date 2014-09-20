package com.joncairo.android.todo;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DataLoader {
	Context mContext;
	String mDataFile;
	
	// delete this after testing
	ArrayList<Todo> mTodos;
	
	// set to do list
	// parameter specifies key value in shared prefs
	// reads sharedprefs returns a ToDoList object
	public void setData(String stringKeyName, ArrayList<Todo> toDosToBeSaved){
        // get the shared prefs object
		Gson gson = new Gson();
		String mSerializedToDos = gson.toJson(toDosToBeSaved);	
		SharedPreferences sp = mContext.getSharedPreferences(mDataFile, 0);
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putString(stringKeyName, mSerializedToDos);
        spEditor.commit();	
	}
	// get to do list obeject from sharedprefs
	// parameter specifies key value in shared prefs
	// reads sharedprefs returns a ToDoList object
	public ArrayList<Todo> getData(String requestedString){
        SharedPreferences sp = mContext.getSharedPreferences(mDataFile, 0);
        String mSerializedString = sp.getString(requestedString, "No Value for ToDosObject");
		Gson gson = new Gson();
        mTodos = gson.fromJson(mSerializedString, new TypeToken<List<Todo>>(){}.getType());
		return mTodos;

	}
	
	// user needs to specify a data file and the context of 
	// the application.
	public DataLoader(Context context, String dataFile){
		mContext = context;
		// this is the main datafile for the application
		mDataFile = dataFile;
        Gson mGson = new Gson();
		// for testing purposes create a Todo list object and
		// save it so we have data to work with
		ToDoList mToDoListObject = new ToDoList(mContext);
        ArrayList<Todo> mToDoListArray = mToDoListObject.getTodos();
        String mserializedData = mGson.toJson(mToDoListArray);

        //SharedPreferences sp = mContext.getSharedPreferences(mDataFile, 0);
        //SharedPreferences.Editor spEditor = sp.edit();
        //spEditor.putString("TO_DO_LIST", mserializedData);
        //spEditor.commit();		
	}
}
