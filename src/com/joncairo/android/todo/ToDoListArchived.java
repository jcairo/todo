package com.joncairo.android.todo;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import com.joncairo.android.todo.Todo;;

// An array containing Todo objects which are classified as archived.
public class ToDoListArchived {
    // create an
	private ArrayList<Todo> mToDosArchived;
    
	private static ToDoListArchived sToDoListArchived;
    private Context mAppContext;
    
    private ToDoListArchived(Context appContext) {
        mAppContext = appContext;
        mToDosArchived = new ArrayList<Todo>();
        // populate with dummy todos
        for (int i = 0; i < 50; i++) {
            Todo t = new Todo("todo archived " + i);
            t.setDone(i % 2 == 0); // Every other one
            mToDosArchived.add(t);
        	}		
    }
    
    // return all the todos in the list
    public ArrayList<Todo> getTodosArchived() {
    	return mToDosArchived;
    }
    
    public Todo getToDoArchived(UUID id) {
        for (Todo t : mToDosArchived) {
            if (t.getToDoId().equals(id))
                return t;
        }
        return null;
    }
    
    public static ToDoListArchived get(Context c) {
        if (sToDoListArchived == null) {
            sToDoListArchived = new ToDoListArchived(c.getApplicationContext());
        }
        return sToDoListArchived;
    }
}
