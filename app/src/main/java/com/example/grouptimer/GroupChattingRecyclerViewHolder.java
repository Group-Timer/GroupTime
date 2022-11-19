package com.example.grouptimer;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GroupChattingRecyclerViewHolder extends RecyclerView.ViewHolder
{
    public TextView ChatConfirm;
    public TextView ChatTime;
    public TextView ChatText;
    public TextView ChatSenderName;


    public GroupChattingRecyclerViewHolder(@NonNull View item)
    {
        super(item);


        ChatConfirm = item.findViewById(R.id.chatConfirm);
        ChatTime = item.findViewById(R.id.chatTime);
        ChatText = item.findViewById(R.id.chatText);
        ChatSenderName = item.findViewById(R.id.chatSenderName);
    }


    public TextView getGroupNameText()
    {
        return this.ChatText;
    }
}