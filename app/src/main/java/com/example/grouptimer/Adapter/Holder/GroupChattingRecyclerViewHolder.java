package com.example.grouptimer.Adapter.Holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grouptimer.Activity.GroupChattingActivity;
import com.example.grouptimer.R;

public class GroupChattingRecyclerViewHolder extends RecyclerView.ViewHolder
{
    public LinearLayout DateLinearLayout;
    public LinearLayout ChatLinearLayout;

    public TextView ChatDate;
    public TextView ChatConfirm;
    public TextView ChatTime;
    public TextView ChatText;
    public TextView ChatSenderName;


    public GroupChattingRecyclerViewHolder(@NonNull View item)
    {
        super(item);


        DateLinearLayout = item.findViewById(R.id.dateLayout);
        ChatLinearLayout = item.findViewById(R.id.chatLayout);
        ChatDate = item.findViewById(R.id.chatDate);
        ChatConfirm = item.findViewById(R.id.chatConfirm);
        ChatTime = item.findViewById(R.id.chatTime);
        ChatText = item.findViewById(R.id.chatText);
        ChatSenderName = item.findViewById(R.id.chatSenderName);


        ChatText.setMaxWidth((int)(GroupChattingActivity.standardSize_X / 2));
    }


    public TextView getGroupNameText()
    {
        return this.ChatText;
    }
}
