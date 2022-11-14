package com.example.grouptimer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EmptyActivity extends AppCompatActivity implements View.OnClickListener {


    Button makeGroup;
    Button personal;
    Button mypage;
    Button changepw;

    Button groupToDoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);


        makeGroup = (Button) findViewById(R.id.makeGroup);
        personal = (Button) findViewById(R.id.personal);
        mypage = findViewById(R.id.mypage);
        changepw = findViewById(R.id.changePassword);
        groupToDoList = (Button) findViewById(R.id.groupToDoList);


        makeGroup.setOnClickListener(this);
        personal.setOnClickListener(this);

        mypage.setOnClickListener(this);
        changepw.setOnClickListener(this);
        groupToDoList.setOnClickListener(this);




    }


    @Override
    public void onClick(View view)
    {
        if(view == makeGroup)
        {
            startActivity(new Intent(this, MakeGroupActivity.class));
        }
        else if(view == personal)
        {
            startActivity(new Intent(this, PersonalTimeTableActivity.class));
        }
<<<<<<< HEAD
        else if(view == mypage){
            startActivity(new Intent(this, MyPageActivity.class));
        }
        else if(view == changepw){
            startActivity(new Intent(this, ChangePWActivity.class));
=======
        else if(view == groupToDoList)
        {
            startActivity(new Intent(this, GroupToDoListActivity.class));
>>>>>>> Hyunwoo
        }
    }
}