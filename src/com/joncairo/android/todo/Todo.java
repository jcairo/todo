package com.joncairo.android.todo;

import java.util.Date;
import java.util.UUID;

public class Todo {
	private String mToDoName;
	private UUID mToDoId;
	private Date mDateCreated;
	private Boolean mDone;
	private Boolean mArchived;
	
	// To-do getters and setters
	public String getToDoName() {
		return mToDoName;
	}
	public void setToDoName(String toDoName) {
		mToDoName = toDoName;
	}
	public Boolean getDone() {
		return mDone;
	}
	public void setDone(Boolean done) {
		mDone = done;
	}
	public Boolean getArchived() {
		return mArchived;
	}
	public void setArchived(Boolean archived) {
		mArchived = archived;
	}
//	public UUID getToDoId() {
//		return mToDoId;
//	}
	public Date getDateCreated() {
		return mDateCreated;
	}
	
	// To-do Constructor
	public Todo(String todoname) {
		//mToDoId = UUID.randomUUID();
		mDateCreated = new Date();
		mDone = false;
		mArchived = false;
		mToDoName = todoname;
	}
	
	public String toString() {
		return mToDoName;
	}
	
	// This method formats the todo info in an email friendly manner
	public String toStringEmailFormat(){
		String toDoEmailable = this.getToDoName();
		if (this.getDone()){
			toDoEmailable += " [x]";
		} else {
			toDoEmailable += " [ ]";
		}
		return toDoEmailable;
	}
	
}
