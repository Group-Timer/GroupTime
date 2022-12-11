package com.example.grouptimer;

import java.util.HashMap;
import java.util.Map;

public class GroupChatRecyclerViewItem
{
    public String Message;
    public String SenderUID;

    public long SendTime;

    public Map<String, Boolean> MemberIndice;

    public int ViewType;

    public String Key;

    public int InvalidValue;


    public GroupChatRecyclerViewItem()
    {

    }


    public GroupChatRecyclerViewItem(Map<String, Boolean> memberIndice, String message, long sendTime, String senderUID)
    {
        this.MemberIndice = memberIndice;
        this.Message = message;
        this.SendTime = sendTime;
        this.SenderUID = senderUID;
        this.InvalidValue = 0;
    }


    public int Get_ViewType()
    {
        return this.ViewType;
    }


    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("MemberIndice", this.MemberIndice);
        result.put("Message", this.Message);
        result.put("SendTime", this.SendTime);
        result.put("SenderUID", this.SenderUID);


        return result;
    }


    public void Change_Data(GroupChatRecyclerViewItem item)
    {
        this.MemberIndice = item.MemberIndice;
    }
}
