package com.example.grouptimer;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

public class GroupRecyclerViewHolder extends RecyclerView.ViewHolder
{
    public TextView GroupNameText;
    public TextView EmptyText;

    public Button ExpansionButton;

    //public RecyclerView ToDoListRecyclerView;

    public TextView GroupScheduleTimeText;

    public ViewPager2 ToDoListViewPagers;

    public ProgressBar ToDoListProgressBar;

    public String GroupIDText;

    public boolean ExpansionButtonClick;


    public GroupRecyclerViewHolder(@NonNull View item)
    {
        super(item);


        GroupNameText = item.findViewById(R.id.groupName);
        ExpansionButton = item.findViewById(R.id.expansion);
        //ToDoListRecyclerView = item.findViewById(R.id.toDoListRecyclerView);
        GroupScheduleTimeText = item.findViewById(R.id.groupScheduleTime);
        ToDoListViewPagers = item.findViewById(R.id.viewPager);
        ToDoListProgressBar = item.findViewById(R.id.toDoListProgressBar);
        EmptyText = item.findViewById(R.id.toDoListEmptyText);

        ExpansionButtonClick = false;
    }


    public TextView getGroupNameText()
    {
        return this.GroupNameText;
    }
}
