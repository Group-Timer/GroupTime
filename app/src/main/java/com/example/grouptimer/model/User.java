package com.example.grouptimer.model;

public class User {
    public String userName; // user 이름
    public int phoneNumber; // user 전화번호
    public int groupNumber;// user 참여중인 group 수
    public String uid;
    public String eMail;

    public User(){

    }

    public  User(String userName, int phoneNumber, String uid){
        this.userName = userName;
        this.phoneNumber=phoneNumber;
        this.uid = uid;
    }

}
