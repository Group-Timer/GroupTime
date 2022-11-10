package com.example.grouptimer;

import android.text.Editable;
import android.widget.CheckBox;
import android.widget.EditText;

public class ToDoListItem {

    String toDoList ;
    boolean check;

    private EditText toDoListEditText;
    private CheckBox toDoListCheckBox;

    public EditText GetToDoListEditText(){
        return toDoListEditText;
    }

    public void setToDoList(EditText toDoListEditText){
        toDoList = toDoListEditText.getText().toString();
    }

    public void setToDoListCheckBox(CheckBox toDoListCheckBox){
        check = toDoListCheckBox.isChecked();
    }

    public void setToDoListString(String toDoListString){
        this.toDoList =toDoListString;
    }

    public void setToDoListEditText(Boolean toDoListCheckBoxBoolean){
        this.check =toDoListCheckBoxBoolean;
    }

    public EditText GetToDoList(){
        return this.toDoListEditText;
    }

    public CheckBox GetToDoCheckBox(){
        return  this.toDoListCheckBox;

    }

    public String GetToDoListString(){
        setToDoList(RecyclerAdapter.ViewHolder.toDoListEditText);
        return this.toDoList;
    }

    public boolean GetToDoListCheckBoxBoolean(){
        setToDoListCheckBox(RecyclerAdapter.ViewHolder.toDoListCheckBox);
        return this.check;
    }

}
