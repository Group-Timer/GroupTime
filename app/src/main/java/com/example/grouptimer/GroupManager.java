package com.example.grouptimer;

import java.util.ArrayList;

public class GroupManager
{
    String insertCode;
    String groupMakerUid;
    ArrayList<String> groupMember;
    String groupName;
    int groupNumber;
    String groupPurpose;
    int memberCnt;


    public GroupManager()
    {

    }

    public GroupManager(String insertCode, String groupMakerUid, ArrayList<String> groupMember, String groupName, int groupNumber, String groupPurpose, int memberCnt)
    {
        this.groupNumber = groupNumber;
        this.memberCnt = memberCnt;
    }
}
