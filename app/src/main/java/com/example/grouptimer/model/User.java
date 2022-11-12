package com.example.grouptimer.model;

import java.util.ArrayList;
import java.util.List;


public class User {
    public String userName; // user 이름
    public int phoneNumber; // user 전화번호


    public String groupNumber; // user 참여중인 group 수
    //public List<String> GroupList = new ArrayList<String>();


    public User(String userName, int phoneNumber)
    {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.groupNumber = Integer.toString(0);
    }
}