package com.example.grouptimer;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GroupToDoListRecyclerViewHolder extends RecyclerView.ViewHolder
{
    public CheckBox todoListCheckBox;

    public TextView todoListText;


    public GroupToDoListRecyclerViewHolder(@NonNull View item)
    {
        super(item);


        todoListCheckBox = item.findViewById(R.id.viewpager2CheckBox1);
        todoListText = item.findViewById(R.id.viewpager2Text1);
    }


    public TextView getGroupNameText()
    {
        return this.todoListText;
    }
}
