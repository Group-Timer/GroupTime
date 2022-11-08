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

    public String GetToDoListString(){
        setToDoList(RecyclerAdapter.ViewHolder.toDoListEditText);
        return this.toDoList;
    }

    public boolean GetToDoCheckBox(CheckBox toDoListCheckBox){
        check= toDoListCheckBox.isChecked();
        return check;
    }

}
