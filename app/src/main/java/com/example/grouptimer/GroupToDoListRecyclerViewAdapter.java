package com.example.grouptimer;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GroupToDoListRecyclerViewAdapter extends RecyclerView.Adapter<GroupToDoListRecyclerViewHolder>
{
    private ArrayList<String> AdapterTodoListText;
    private ArrayList<Boolean> AdapterTodoListCheckBox;

    private ViewGroup parent;


    public GroupToDoListRecyclerViewAdapter(ArrayList<String> todoListText, ArrayList<Boolean> todoListCheckBox)
    {
        this.AdapterTodoListText = todoListText;
        this.AdapterTodoListCheckBox = todoListCheckBox;
    }


    @NonNull
    @Override
    public GroupToDoListRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        this.parent = parent;


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_todolist_recyclerview_item, parent, false);

        GroupToDoListRecyclerViewHolder viewHolder = new GroupToDoListRecyclerViewHolder(view);


        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull GroupToDoListRecyclerViewHolder viewHolder, int position)
    {
        String text = this.AdapterTodoListText.get(position);
        boolean checked = this.AdapterTodoListCheckBox.get(position);


        viewHolder.todoListText.setText(text);
        viewHolder.todoListCheckBox.setChecked(checked);
        viewHolder.todoListCheckBox.setEnabled(false);
    }


    @Override
    public int getItemCount()
    {
        return this.AdapterTodoListText.size();
    }
}
