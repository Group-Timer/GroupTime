package com.example.grouptimer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EmptyActivity extends AppCompatActivity implements View.OnClickListener {


    Button makeGroup;
    Button personal;
    Button groupToDoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);


        makeGroup = (Button) findViewById(R.id.makeGroup);
        personal = (Button) findViewById(R.id.personal);
        groupToDoList = (Button) findViewById(R.id.groupToDoList);


        makeGroup.setOnClickListener(this);
        personal.setOnClickListener(this);
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
        else if(view == groupToDoList)
        {
            startActivity(new Intent(this, GroupToDoListActivity.class));
        }
    }
}