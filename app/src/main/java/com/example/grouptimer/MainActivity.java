package com.example.grouptimer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button register;
    private Button login;
    private Button findID;

    private Button TempButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        register = (Button) findViewById(R.id.register);
        login = (Button) findViewById(R.id.login);

        findID = findViewById(R.id.findID);


        register.setOnClickListener(this);
        login.setOnClickListener(this);

        findID.setOnClickListener(this);
    }


    @Override
    public void onClick(View view)
    {
        if(view == register)
        {
            startActivity(new Intent(this, RegisterActivity.class));
        }
        else if(view == login)
        {
            startActivity(new Intent(this, LoginActivity.class));
        }
        else if(view == findID) {
            startActivity(new Intent(this, SearchIDActivity.class));
        }
        else if(view == TempButton)
        {
            int testA;
        }
    }
}