package com.joncairo.android.todo;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import com.joncairo.android.todo.Todo;;

public class ToDoList {
    // create an
	private ArrayList<Todo> mToDos;
    
	private static ToDoList sToDoList;
    private Context mAppContext;
    
    private ToDoList(Context appContext) {
        mAppContext = appContext;
        mToDos = new ArrayList<Todo>();
        // populate with dummy todos
        for (int i = 0; i < 4; i++) {
            Todo t = new Todo("todo" + i);
            t.setDone(i % 2 == 0); // Every other one
            mToDos.add(t);
        	}		
    }
    
    // return all the todos in the list
    public ArrayList<Todo> getTodos() {
    	return mToDos;
    }
    
    public Todo getToDo(UUID id) {
        for (Todo t : mToDos) {
            if (t.getToDoId().equals(id))
                return t;
        }
        return null;
    }
    
    public static ToDoList get(Context c) {
        if (sToDoList == null) {
            sToDoList = new ToDoList(c.getApplicationContext());
        }
        return sToDoList;
    }
    
    public void add(Todo newToDo){
    	mToDos.add(newToDo);
    }
}
