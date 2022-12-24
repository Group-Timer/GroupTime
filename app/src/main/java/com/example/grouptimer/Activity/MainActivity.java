package com.example.grouptimer.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.grouptimer.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button register;
    private Button login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        register = (Button) findViewById(R.id.register);
        login = (Button) findViewById(R.id.login);


        register.setOnClickListener(this);
        login.setOnClickListener(this);

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
    }
}