package com.joncairo.android.todo;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import com.google.gson.Gson;

public class DataLoader {
	// set to do list
	// parameter specifies key value in shared prefs
	// reads sharedprefs returns a ToDoList object
	public void setToDoList(String string, Context c){
		ToDoList t = new ToDoList(c);
		Gson gson = new Gson();
		
	}
	// get archived to do list
	// parameter specifies key value in shared prefs
	// reads sharedprefs returns a ToDoList object
	public void getToDoList(String string){
		
	}
}
