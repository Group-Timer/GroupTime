package com.example.grouptimer.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    public String eMail;
    public int groupNumber;// user 참여중인 group 수
    public int phoneNumber; // user 전화번호
    public String userName; // user 이름

    public User(){

    }

    public  User( String eMail, int groupNumber, int phoneNumber,String userName ){
        this.userName = userName;
        this.phoneNumber=phoneNumber;
        this.eMail = eMail;
        this.groupNumber = groupNumber;
    }

    public int getGroupNumber() {
        return groupNumber;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public String geteMail() {
        return eMail;
    }

    public String getUserName() {
        return userName;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
