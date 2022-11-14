package com.example.grouptimer;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GroupRecyclerViewHolder extends RecyclerView.ViewHolder
{
    public TextView GroupNameText;

    public Button ExpansionButton;

    public String GroupIDText;


    public GroupRecyclerViewHolder(@NonNull View item)
    {
        super(item);


        GroupNameText = item.findViewById(R.id.groupName);
        ExpansionButton = item.findViewById(R.id.expansion);
    }


    public TextView getGroupNameText()
    {
        return this.GroupNameText;
    }
}
