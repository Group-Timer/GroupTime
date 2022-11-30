package com.example.grouptimer;

import android.content.Context;
import android.text.Editable;
import android.widget.CheckBox;
import android.widget.EditText;

public class ToDoListItem {

    String toDoList ;
    Boolean check;

    public EditText toDoListEditText;
    public CheckBox toDoListCheckBox;

    ToDoListItem(Context context, String text , Boolean check){
        this.toDoListEditText = new EditText(context);
        this.toDoListCheckBox = new CheckBox(context);

        this.toDoList = text;
        this.check = check;

        this.toDoListEditText.setText(this.toDoList);
        this.toDoListCheckBox.setChecked(this.check);
    }


    public String TodoList(){
        return this.toDoList;
    }

    public Boolean Check(){
        return this.check;
    }

    public EditText GetToDoListEditText(){
        return toDoListEditText;
    }

    public String setToDoList(){
        toDoList = toDoListEditText.getText().toString();
        return toDoList;
    }

    public Boolean setToDoListCheckBox(CheckBox toDoListCheckBox){
        check = toDoListCheckBox.isChecked();
        return check;
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
        setToDoList();
        return this.toDoList;
    }

//    public boolean GetToDoListCheckBoxBoolean(){
//        setToDoListCheckBox(RecyclerAdapter.ViewHolder.toDoListCheckBox);
//        return this.check;
//    }

}
