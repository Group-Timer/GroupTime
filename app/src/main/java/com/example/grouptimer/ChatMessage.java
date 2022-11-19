package com.example.grouptimer;

import java.util.Map;

public class ChatMessage
{
    public String Message;
    public String SenderUID;

    public int SendTime;

    public Map<String, Boolean> MemberIndice;


    public ChatMessage()
    {

    }


    public ChatMessage(String message, String senderUID, int sendTime, Map<String, Boolean> memberIndice)
    {
        this.Message = message;
        this.SenderUID = senderUID;
        this.SendTime = sendTime;
        this.MemberIndice = memberIndice;
    }
}
