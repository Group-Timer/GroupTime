package com.example.grouptimer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

public class GroupChattingRecyclerViewAdapter extends RecyclerView.Adapter<GroupChattingRecyclerViewHolder>
{
    public ArrayList<GroupChatRecyclerViewItem> AdapterChatList;

    public ViewGroup parent;


    public GroupChattingRecyclerViewAdapter(ArrayList<GroupChatRecyclerViewItem> chatList)
    {
        this.AdapterChatList = chatList;
    }


    @NonNull
    @Override
    public GroupChattingRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = null;
        GroupChattingRecyclerViewHolder viewHolder = null;


        this.parent = parent;


        if(viewType == DefineValue.Chat_Left)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chatting_left_recyclerview_item, parent, false);

            viewHolder = new GroupChattingRecyclerViewHolder(view);
        }
        else if(viewType == DefineValue.Chat_Right)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chatting_right_recyclerview_item, parent, false);

            viewHolder = new GroupChattingRecyclerViewHolder(view);

            viewHolder.ChatSenderName.setVisibility(View.GONE);
        }


        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull GroupChattingRecyclerViewHolder viewHolder, int position)
    {
        String message;

        String sendTime;
        String hour;
        String minute;
        String time;

        String senderUID;
        String senderName;

        Map<String, Boolean> memberIndice;

        int indiceCnt;


        message = this.AdapterChatList.get(position).Message;
        sendTime = Integer.toString(this.AdapterChatList.get(position).SendTime);
        senderUID = this.AdapterChatList.get(position).SenderUID;
        memberIndice = this.AdapterChatList.get(position).MemberIndice;


        hour = sendTime.substring(4, 6);
        minute = sendTime.substring(6, 8);


        /*
        if(Integer.parseInt(minute) < 10)
        {
            time = hour + ":" + "0" + minute;
        }
        else
        {
            time = hour + ":" + minute;
        }
         */

        time = hour + ":" + minute;


        senderName = GroupTimeTableActivity.MemberInfo.get(senderUID);


        indiceCnt = 0;
        for(int i = 0; i < GroupTimeTableActivity.MemberIDList.size(); i++)
        {
            String memberUID;


            if(GroupTimeTableActivity.MemberIDList.get(i) != null)
            {
                memberUID = GroupTimeTableActivity.MemberIDList.get(i);

                if(memberIndice.get(memberUID) != null)
                {
                    if(memberIndice.get(memberUID) == false)
                    {
                        indiceCnt++;
                    }
                }
            }
        }


        viewHolder.ChatText.setText(message);
        viewHolder.ChatTime.setText(time);
        viewHolder.ChatSenderName.setText(senderName);

        if(indiceCnt > 0)
        {
            viewHolder.ChatConfirm.setText(Integer.toString(indiceCnt));
        }
    }


    @Override
    public int getItemCount()
    {
        return this.AdapterChatList.size();
    }


    @Override
    public int getItemViewType(int position) {
        return this.AdapterChatList.get(position).Get_ViewType();
    }
}
